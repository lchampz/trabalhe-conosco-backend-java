# Sistema de Cartórios — Docket

Cadastro de cartórios e das certidões que cada um emite, com telas em Thymeleaf e API REST.
Implementação do desafio da Docket ([enunciado original](#enunciado-original) no fim deste arquivo).

## Como rodar

### Opção 1 — Docker (recomendado)

Único pré-requisito: Docker com Compose v2. Não precisa de Java nem Maven instalados.

```bash
docker compose up --build -d
```

A aplicação sobe em **http://localhost:8080** já com dados de exemplo carregados.

Para acompanhar os logs:

```bash
docker compose logs -f
```

Para derrubar:

```bash
docker compose down
```

### Opção 2 — Local (Maven)

Pré-requisitos: JDK 17 e nada mais (o Maven vem pelo wrapper).

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)   # macOS
./mvnw spring-boot:run
```

No fish shell, a primeira linha é `set -x JAVA_HOME (/usr/libexec/java_home -v 17)`.

Para subir em outra porta:

```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=8090
```

## O que está disponível

| O quê | Endereço |
|---|---|
| Telas (Thymeleaf) | http://localhost:8080/ |
| Cartórios | http://localhost:8080/cartorios |
| Certidões | http://localhost:8080/certidoes |
| Swagger UI | http://localhost:8080/swagger-ui/index.html |
| OpenAPI JSON | http://localhost:8080/v3/api-docs |
| Console do H2 | http://localhost:8080/h2-console |

Credenciais do H2: JDBC URL `jdbc:h2:mem:cartorios`, usuário `sa`, senha em branco.

## Dados de exemplo

O banco é H2 em memória e é recriado a cada inicialização. Uma migration repetível do Flyway
(`R__seed.sql`) popula automaticamente **3 cartórios** e **5 certidões**, com vínculos
propositalmente desiguais entre eles — assim dá para testar tanto um cartório que emite
várias certidões quanto um que emite só uma.

## API REST

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/api/v1/cartorio?page&size&nome` | Lista paginada, com filtro opcional por nome |
| `GET` | `/api/v1/cartorio/{id}` | Detalhe de um cartório |
| `GET` | `/api/v1/cartorio/{id}/certidoes` | Certidões emitidas pelo cartório |
| `POST` | `/api/v1/cartorio` | Cadastra |
| `PUT` | `/api/v1/cartorio/{id}` | Atualiza |
| `DELETE` | `/api/v1/cartorio/{id}` | Exclui |
| `PUT` | `/api/v1/cartorio/{cartorioId}/certidoes/{certidaoId}` | Vincula uma certidão |
| `DELETE` | `/api/v1/cartorio/{cartorioId}/certidoes/{certidaoId}` | Desvincula uma certidão |
| `GET` | `/api/v1/certidao?page&size&nome` | Lista paginada |
| `GET` | `/api/v1/certidao/{id}` | Detalhe |
| `POST` | `/api/v1/certidao` | Cadastra |
| `PUT` | `/api/v1/certidao/{id}` | Atualiza |
| `DELETE` | `/api/v1/certidao/{id}` | Exclui |

Exemplo de cadastro:

```bash
curl -X POST http://localhost:8080/api/v1/cartorio \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "3º Ofício de Notas",
    "cep": "01310100",
    "rua": "Avenida Paulista",
    "numero": "900",
    "complemento": "Conjunto 45",
    "bairro": "Bela Vista",
    "cidade": "São Paulo",
    "uf": "SP",
    "certidoes": [1, 2]
  }'
```

Erros seguem RFC 7807 (`ProblemDetail`), com um `codigo` estável no corpo e, nos erros de
validação, uma lista `errors` com um item por campo inválido.

## Como está organizado

```
controller/          → API REST (@RestController)
controller/web/      → telas Thymeleaf (@Controller)
client/              → clients HTTP que consomem a própria API REST
service/             → regras de negócio
repository/          → Spring Data JPA
model/               → entidades
dto/                 → records de request/response
mapper/              → MapStruct
shared/error/        → tratamento de erro (ProblemDetail)
```

**As telas consomem a própria API REST.** O `CartorioWebController` não chama o `Service`
direto: ele usa o `CartorioApiClient`, que faz uma chamada HTTP para `/api/v1/cartorio`.
Isso mantém a API como única porta de entrada das regras e comprova que ela funciona
ponta a ponta. O custo é uma ida e volta de rede por tela — aceitável aqui, mas em produção
o front seria um deploy separado consumindo a API diretamente do navegador.

O banco é versionado com Flyway e o Hibernate roda com `ddl-auto=validate`, ou seja, o schema
nunca é gerado pela aplicação: qualquer divergência entre entidade e migration derruba a
inicialização em vez de passar despercebida.

## Stack

Java 17 · Spring Boot 3.3.2 · Thymeleaf · Spring Data JPA · Flyway · H2 · MapStruct · Lombok · springdoc-openapi

## Testes

```bash
./mvnw test
```

## Problemas comuns

**Porta 8080 ocupada.** Descubra quem está usando com `lsof -nP -iTCP:8080 -sTCP:LISTEN`.
Para subir em outra porta no Docker, altere o mapeamento em `docker-compose.yml`
(por exemplo `"8081:8080"`).

**Mudei o código e o container continua com a versão antiga.** `docker compose up` reaproveita
a imagem já existente e reiniciar o container não reconstrói nada. Use:

```bash
docker compose up --build -d --force-recreate
```

**`mvn` reclama da versão do Java.** O projeto exige JDK 17. Confirme com `java -version` e,
se a JDK padrão da máquina for outra, aponte o `JAVA_HOME` como mostrado acima.

---

## Enunciado original

## Proposta
Neste teste para a área de desenvolvedor Backend, à partir do briefing e requisito apresentados, a Docket propõe a você construir um sistema de cartórios e o planejamento do seu projeto.

## Briefing
Uma empresa com a proposta de desburocratizar os serviços cartorários para pessoas físicas e pequenas empresas, e de reduzir o tempo de entrega dos documentos aos seus clientes. 

## O que fazemos
- Buscam os documentos em todo o Brasil
- Emitem 2ª vias de certidões de nascimento, casamento e óbito
- Entregam os documentos aos clientes em até 15 dias

## Requisitos

### História do Usuário
Como operador do sistema, quando entro em contato com um novo cartório, desejo cadastrá-lo na nossa base com suas informações básicas, pois assim posso consultá-lo e alterá-lo sempre que necessário.

### Requisitos Obrigatórios:
Utilizando **Thymeleaf** ou **JSP (com JSTL)** no front-end (preferencialmente **Thymeleaf**), o sistema deve:

 - Permitir o cadastro das informações básicas dos cartórios, que são: nome, endereço e as certidões que ele emite.
 - Listar os cartórios e suas informações. 
 - Permitir a exclusão e edição dos cartórios.

### Requisitos Opcionais (diferenciais)

#### Consumo de API Rest:
- Consumir via backend a lista de certidões de nossa API REST https://docketdesafiobackend.herokuapp.com/api/v1/certidoes e relacioná-las com os cartórios.

#### Disponibilizar um client API Rest (seguindo as boas práticas da Arquitetura Rest) que deve:
- Permitir o cadastro das informações básicas dos cartórios, que são: nome, endereço e as certidões que ele emite.
- Listar os cartórios e suas informações.
- Permitir a exclusão e edição dos cartórios.

### Informações Adicionais
 - Um cartório tem apenas um nome e endereço. 
 - Um cartório pode emitir uma ou mais certidões. 
 - Uma certidão possui apenas um nome.

### Requisitos de Tecnologias
Usar as Seguintes Tecnologias:
- Java 8 (ou superior)
- Spring Boot ou Spring MVC 
- Thymeleaf HTML ou JSP (com JSTL)
- Banco de dados H2DB ou Postgres

## Planejamento
Nos conte como irá se planejar para executar o projeto, como por exemplo: como transformou os requisitos em tarefas, se utilizou alguma ferramenta para se organizar, se desenhou algum diagrama e etc.

# Como participar
- Crie um repositório público no github e coloque o código fonte do projeto.
 - Envie por email o seguinte conteúdo:
    - Link do repositório do github com o código fonte do projeto
    - Descrição do planejamento
    - Currículo em anexo ou link do perfil no linkedin.com

Se ficou com alguma dúvida estamos à disposição.


