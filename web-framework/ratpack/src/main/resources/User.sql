DROP TABLE IF EXISTS USER;
CREATE TABLE USER
(
   ID      BIGINT AUTO_INCREMENT PRIMARY KEY,
   TITLE   VARCHAR(255),
   NAME    VARCHAR(255),
   COUNTRY VARCHAR(255)
);

INSERT INTO USER
VALUES (1, 'Mr', 'Norman Potter', 'USA');
INSERT INTO USER
VALUES (2, 'Miss', 'Ketty Smith', 'FRANCE');