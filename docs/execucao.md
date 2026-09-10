## Plano de execução

A ordem de execucao seguiu essa linha, cada fase foi pensada a modo de que a posterior depende da anterior e assim conseguimos um fluxo continuo de código sem precisar ficar "indo e voltando" em arquivos diferentes (a menos por ajustes, claro):

1 - DB
  Estruturar o banco de dados é o primeiro passo afim d entender o que será armazenado e distribuido na aplicação, primeiro foi definir o H2 (banco inMemory) por se tratar de poucos dados, facilita instanciar um container no docker (n vai precisar d uma instancia d db)

2 - Migration
  Versionamento do nosso banco de dados afim de reprodutibilidade e rastreabilidade, alterações devem passar por aqui afim de sincronizar com outras instancias da aplicacao (seja outros devs ou os proprios servers)

3 - Model
  Nossa interface para o banco de dados a nível de aplicação, aqui temos uma classe representando uma entidade no DB, como estamos usando SB, utilizamos a annotation @Table com o nome da tabela para que o proxy do spring consiga conectar ao nosso H2, encapsulo ali algumas regras de negócio que julgo serem de interesse da entidade em si (como por ex. cartorio saber como adicionar/remover novas certidoes), afim de que ela saiba se auto-validar e como se manipular, a nível de memória, todo acesso ao banco em si é feito pelo repository

4 - DTOs
  Fecho meus contratos de trafego de dados, para preservar imutabilidade na aplicacao, nunca trefego a model entre layers, utilizo records sanitizados para isso, aqui defino request e response já pensando na minha controller

5 - Repository
  Nossa camada de acesso a dados, usando JPARepository, herdo alguns métodos de acesso a dados como findById, como espero receber um Page<> ao inves de List, re-escrevo alguns metodos, usando entityGraph para evitar N+1 e problema com lazy

6 - Service
  Como nosso acesso a dados e nosso contrato de trafego esta pronto, consigo inserir a camada de regra de negócio, aqui faço os cruds, respeitando as regras de negócio e conciliando as outras layers

7 - Controllers
  Acesso externo com a lógica da aplicação, aqui defino endpoints, recebo a informação (payload) do consumer da API, já valido a lógica q defini nos DTOs de request, passo pra camada de servico e respondo ao client

8 - Client
  Vai consumir nossa API rest (só necessário aqui pois foi definido que seriam APIs rest e nao SSR)

9 - Apresentaçao (thymeleaf)
  Camada de apresentação, aqui tem as listagens e forms para o crud, o usuário aqui tem o contato com a aplicação

10 - Testes unitários
  Testes unitários nas camadas mais sensiveis da aplicação: controller (porta da nossa aplicacao pro mundo externo) e services (quem de fato vai validar a regra de negócio), apliquei testes no caminho feliz e em póssiveis erros (resource not found, bad request etc)

Extra (Shared)
  Shared acabei construindo sob demanda, o ErrorHandler foi por refinamento do service/controller, adicionar o PageResponse para injetar nativamente a contagem e paginacao e o JpaAuditConfig é pq nos testes unitários eu não podia embalar toda aplicacao no @EnableJpaAudit (p preencher o createdAt), entao extrai num bean de configuracao e usei sob demanda
