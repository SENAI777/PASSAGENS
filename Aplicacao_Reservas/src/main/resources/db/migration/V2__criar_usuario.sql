CREATE TABLE usuario (
    id_usuario INT          NOT NULL AUTO_INCREMENT,
    usuario    VARCHAR(50)  NOT NULL,
    senha      VARCHAR(100) NOT NULL,
    PRIMARY KEY (id_usuario),
    UNIQUE KEY usuario (usuario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT IGNORE INTO usuario (usuario, senha) VALUES ('admin', 'admin');
