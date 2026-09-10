INSERT INTO certidao (nome) VALUES
    ('Certidão de Nascimento'),
    ('Certidão de Casamento'),
    ('Certidão de Óbito'),
    ('Escritura Pública'),
    ('Procuração');

INSERT INTO cartorio (nome, cep, rua, numero, complemento, bairro, cidade, uf) VALUES
    ('1º Ofício de Registro Civil', '01302000', 'Rua da Consolação', '100', NULL, 'Consolação', 'São Paulo', 'SP'),
    ('2º Ofício de Notas', '20040901', 'Avenida Rio Branco', '156', 'Sala 1201', 'Centro', 'Rio de Janeiro', 'RJ'),
    ('Tabelionato de Notas', '90020008', 'Rua dos Andradas', '1234', NULL, 'Centro Histórico', 'Porto Alegre', 'RS');

INSERT INTO cartorio_certidao (cartorio_id, certidao_id)
SELECT c.id, d.id FROM cartorio c, certidao d
WHERE c.nome = '1º Ofício de Registro Civil' AND d.nome IN ('Certidão de Nascimento', 'Certidão de Casamento', 'Certidão de Óbito');

INSERT INTO cartorio_certidao (cartorio_id, certidao_id)
SELECT c.id, d.id FROM cartorio c, certidao d
WHERE c.nome = '2º Ofício de Notas' AND d.nome IN ('Escritura Pública', 'Procuração');

INSERT INTO cartorio_certidao (cartorio_id, certidao_id)
SELECT c.id, d.id FROM cartorio c, certidao d
WHERE c.nome = 'Tabelionato de Notas' AND d.nome IN ('Escritura Pública');
