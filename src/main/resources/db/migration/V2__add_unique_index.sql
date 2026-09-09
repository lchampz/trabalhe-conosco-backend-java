
ALTER TABLE certidao ADD COLUMN nome_lower VARCHAR(150) GENERATED ALWAYS AS (LOWER(nome));
CREATE UNIQUE INDEX uk_certidao_nome ON certidao (nome_lower);
