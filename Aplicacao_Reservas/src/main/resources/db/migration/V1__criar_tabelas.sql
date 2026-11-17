-- =====================================================================
-- V1 - Estrutura inicial do sistema de reservas de voo
-- Executada automaticamente pelo Flyway ao iniciar a aplicação.
-- NUNCA altere este arquivo depois de aplicado: para mudanças, crie
-- um novo arquivo V2__descricao.sql, V3__descricao.sql, ...
-- =====================================================================

CREATE TABLE passageiro (
    id_passageiro INT          NOT NULL AUTO_INCREMENT,
    nome          VARCHAR(100) NOT NULL,
    email         VARCHAR(150) NOT NULL,
    cpf           VARCHAR(14)  NOT NULL,
    idade         INT          NOT NULL,
    PRIMARY KEY (id_passageiro),
    UNIQUE KEY email (email),
    UNIQUE KEY cpf (cpf),
    CONSTRAINT passageiro_chk_1 CHECK (idade >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE voo (
    id_voo       INT          NOT NULL AUTO_INCREMENT,
    origem       VARCHAR(100) NOT NULL,
    destino      VARCHAR(100) NOT NULL,
    horario      DATETIME     NOT NULL,
    modelo_aviao VARCHAR(100) NOT NULL,
    PRIMARY KEY (id_voo),
    CONSTRAINT voo_chk_1 CHECK (origem <> destino)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE reserva (
    id_reserva    INT           NOT NULL AUTO_INCREMENT,
    id_passageiro INT           NOT NULL,
    id_voo        INT           NOT NULL,
    tarifa        DECIMAL(10,2) NOT NULL,
    qtd_bagagens  INT           NOT NULL DEFAULT 0,
    localizador   VARCHAR(20)   NOT NULL,
    data_reserva  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status        VARCHAR(20)   NOT NULL DEFAULT 'Confirmada',
    PRIMARY KEY (id_reserva),
    UNIQUE KEY localizador (localizador),
    KEY id_voo (id_voo),
    KEY idx_reserva_passageiro (id_passageiro),
    CONSTRAINT reserva_ibfk_1 FOREIGN KEY (id_passageiro) REFERENCES passageiro (id_passageiro),
    CONSTRAINT reserva_ibfk_2 FOREIGN KEY (id_voo) REFERENCES voo (id_voo),
    CONSTRAINT reserva_chk_1 CHECK (tarifa >= 0),
    CONSTRAINT reserva_chk_bagagens CHECK (qtd_bagagens >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
