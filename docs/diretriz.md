## Ideia
Como operador de sistema, devo conseguir cadastrar um novo cartório na base de dados com suas informações básicas e então consulta-lo e alterá-lo sempre que necessário, criar novas certidoes e relaciona-los com o cartório

*nomes podem ser melhorados, aqui é conceito*
## Obrigatório DB:
- Cartorio: id, nome, rua, numero, cidade, UF, CEP, createdAt
- certidao: id, nome, createdAt
- CartorioCertidao: idCartorio, idCertidao @ manyToMany (tabela explosao)
- H2

## Obrigatório JAVA:

  - Simplicidade -> package-by-layer, camadas
  -  ptBr em regra de negocio (p facilitar o entendimento), ingles em funcao metodo etc
  - Crud de cartório com os dados: nome, endereco e certidoes que ele emite
- Crud de certidao: apenas o nome
 - Api rest para servir os endpoints
  - Paginacao ~itens > 10
  - Handler "bonito" d exception
  - Validar dado de entrada (vazio, invalido etc)
  - client interno consumindo nossa api p carregar o thymeleaf
  - testes nas controllers e services
- Java 17, Spring Boot, Thymeleaf e h2

## Requisitos Gerais:
- Semantica
- Organizacao
- Projeto deve ser dockerizado afim de conseguir ser executado completamente, enfase em facilidade de configuracao, reprodutibilidade e preenchimento de todos os requisitos.



