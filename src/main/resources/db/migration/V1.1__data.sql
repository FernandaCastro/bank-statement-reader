INSERT INTO BANK (ID, NAME) VALUES(1, 'Banco do Brasil');

INSERT INTO CLIENT (ID, NAME) VALUES(1, 'Maria');

INSERT INTO STATEMENT_CONFIG (ID, CLIENT_ID, BANK_ID, DESCRIPTION_FIELD, TRANSACTION_DATE_FIELD, TRANSACTION_VALUE_FIELD, DOCUMENT_ID_FIELD)
VALUES(1, 1, 1, 'Histórico', 'Data', 'Valor', 'Número do documento');

INSERT INTO STATEMENT_CONFIG_CATEGORY (ID, STATEMENT_CONFIG_ID, NAME, TAGS)
VALUES  (1, 1, 'income', 'Benefício INSS, Crédito em conta'),
        (2, 1, 'home', 'LIGHT,CEDAE,BB Seguro Auto,TELEMAR,LIGHT,VIVO RJ,SKY SERV,Tarifa Pacote de Serv,DOCinternet'),
        (3, 1, 'supermarket', 'SUPERMERC'),
        (4, 1, 'personal', 'SUPERMERC, Compra com Cart'),
        (5, 1, 'creditcard', 'BB ADMINISTRADORA DE CARTOES'),
        (6, 1, 'beachHouse', 'OK NEWS PROVEDOR,AGUAS DE JUTURNAIBA,JORGE LU,AMPLA');