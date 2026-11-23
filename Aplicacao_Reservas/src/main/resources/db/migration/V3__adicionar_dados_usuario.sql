ALTER TABLE usuario
    ADD COLUMN nome VARCHAR(100) NULL AFTER usuario,
    ADD COLUMN cpf VARCHAR(14) NULL AFTER nome,
    ADD UNIQUE KEY uk_usuario_cpf (cpf);
