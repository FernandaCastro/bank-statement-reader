package com.example.statement;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SpringBootTest(classes = BankStatementApplication.class)
public class StatementServiceTest {

    @Autowired
    private StatementService statementService;


    @Test
    public void testRead() {
        String content = "\"Data\",\"Dependencia Origem\",\"Histórico\",\"Data do Balancete\",\"Número do documento\",\"Valor\",\n" +
                "\"28/10/2020\",\"\",\"Saldo Anterior\",\"\",\"0\",\"1750.31\",";

        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "statement.csv",
                MediaType.TEXT_PLAIN_VALUE,
                content.getBytes(StandardCharsets.ISO_8859_1)
        );

        var bank = new Bank();
        bank.setId("BB");

        try {
            List<StatementTransaction> transactions = statementService.read(bank, file);

            Assertions.assertNotNull(transactions);
            Assertions.assertEquals(1, transactions.size());
            Assertions.assertEquals("28/10/2020", transactions.get(0).getTransactionDate());
            Assertions.assertEquals("Saldo Anterior", transactions.get(0).getDescription());
            Assertions.assertEquals(1750.31, transactions.get(0).getTransactionValue());

        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }


    @Test
    public void testCategorizeHome() {
        List<StatementTransaction> transactions = new ArrayList<>();

        transactions.add(new StatementTransaction("01/12/2020", "Pgto conta água - CEDAE FIDC", -123.39, "1"));
        transactions.add(new StatementTransaction("07/12/2020", "Tarifa Pacote de Serviços - Cobrança referente 07/12/2020", -21.20, "2"));
        transactions.add(new StatementTransaction("07/12/2020", "BB Seguro Auto - SEGURO AUTO BB/MAPFRE", -300.80, "3"));
        transactions.add(new StatementTransaction("10/12/2020", "Pagto conta telefone - TELEMAR RJ (OI FIXO)", -87.86, "4"));
        transactions.add(new StatementTransaction("10/12/2020", "Pagamento conta luz - LIGHT", -90.59, "5"));
        transactions.add(new StatementTransaction("10/12/2020", "Pagamento conta luz - LIGHT", -13.93, "6"));
        transactions.add(new StatementTransaction("14/12/2020", "Pagto conta telefone - VIVO RJ", -173.45, "7"));

        var owner = new Owner();
        owner.setId("maria");
        var bank = new Bank();
        bank.setId("BB");
        try {
            Map<String, Double> soma = statementService.categorize(owner, bank, transactions);
            Assertions.assertEquals(-811.22, soma.get("HOME"), 0);
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testCategorizeBeachHouse() {
        List<StatementTransaction> transactions = new ArrayList<>();
        transactions.add(new StatementTransaction("10/12/2020", "Pagamento de Boleto - OK NEWS PROVEDOR E COMERCIO DE PRODUTO", -80.12, "1"));
        transactions.add(new StatementTransaction("15/12/2020", "Pgto conta água - CONC. AGUAS DE JUTURNAIBA", -20.40, "2"));
        transactions.add(new StatementTransaction("21/12/2020", "Emissão de DOC -  JORGE LU 006/006M", -150.00, "3"));

        var owner = new Owner();
        owner.setId("maria");
        var bank = new Bank();
        bank.setId("BB");
        try {
            Map<String, Double> soma = statementService.categorize(owner, bank, transactions);
            Assertions.assertEquals(-250.52, soma.get("BEACHHOUSE"), 0);
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testCategorizeSupermarket() {
        List<StatementTransaction> transactions = new ArrayList<>();
        transactions.add(new StatementTransaction("11/12/2020", "Compra com Cartão - 11/12 11:26 SUPERMERCADO ESPERAN", -132.90, "1"));
        transactions.add(new StatementTransaction("15/12/2020", "Compra com Cartão - 15/12 11:32 PRINCESA SUPERMERCAD", -145.62, "2"));
        transactions.add(new StatementTransaction("21/12/2020", "Compra com Cartão - 19/12 12:36 DRICA MODA INTIMA LT", -101.00, "8"));
        transactions.add(new StatementTransaction("30/12/2020", "Compra com Cartão - 30/12 15:11 PRINCESA SUPERMERCAD", -370.66, "3"));
        transactions.add(new StatementTransaction("31/12/2020", "Compra com Cartão - 31/12 12:29 SUPERMERCADO ESPERAN", -101.02, "4"));

        var owner = new Owner();
        owner.setId("maria");
        var bank = new Bank();
        bank.setId("BB");
        try {
            Map<String, Double> soma = statementService.categorize(owner, bank, transactions);
            Assertions.assertEquals(-750.20, soma.get("SUPERMARKET"), 0);
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }


    @Test
    public void testCategorizePersonal() {
        List<StatementTransaction> transactions = new ArrayList<>();
        transactions.add(new StatementTransaction("21/12/2020", "Compra com Cartão - 19/12 12:36 DRICA MODA INTIMA LT", -101.00, "1"));
        transactions.add(new StatementTransaction("31/12/2020", "Compra com Cartão - 31/12 12:29 SUPERMERCADO ESPERAN", -50.00, "6"));
        transactions.add(new StatementTransaction("21/12/2020", "Compra com Cartão - 19/12 12:36 CAROL MODA PRAIA", -101.00, "5"));

        var owner = new Owner();
        owner.setId("maria");
        var bank = new Bank();
        bank.setId("BB");
        try {
            Map<String, Double> soma = statementService.categorize(owner, bank, transactions);
            Assertions.assertEquals(-202.00, soma.get("PERSONAL"), 0);
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testCategorizeCreditCard() {
        List<StatementTransaction> transactions = new ArrayList<>();
        transactions.add(new StatementTransaction("10/12/2020", "Pagamento de Boleto - BB ADMINISTRADORA DE CARTOES", -402.35, "1"));
        var owner = new Owner();
        owner.setId("maria");
        var bank = new Bank();
        bank.setId("BB");
        try {
            Map<String, Double> soma = statementService.categorize(owner, bank, transactions);
            Assertions.assertEquals(-402.35, soma.get("CREDITCARD"), 0);
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testCategorizeMix() {
        List<StatementTransaction> transactions = new ArrayList<>();
        transactions.add(new StatementTransaction("30/11/2020", "Saldo Anterior", 1814.10, "0"));
        transactions.add(new StatementTransaction("03/12/2020", "Benefício INSS", 4394.95, "1"));
        transactions.add(new StatementTransaction("04/12/2020", "TED-Crédito em Conta -  HOT ITAIPU ALI", 1045.01, "2"));
        transactions.add(new StatementTransaction("07/12/2020", "BB Seguro Auto - SEGURO AUTO BB/MAPFRE", -300.80, "3"));
        transactions.add(new StatementTransaction("21/12/2020", "Compra com Cartão - 19/12 12:36 DRICA MODA INTIMA LT", -101.00, "4"));
        transactions.add(new StatementTransaction("15/12/2020", "Pgto conta água - CONC. AGUAS DE JUTURNAIBA", -80.46, "5"));
        transactions.add(new StatementTransaction("31/12/2020", "Compra com Cartão - 31/12 12:29 SUPERMERCADO ESPERAN", -101.02, "6"));
        transactions.add(new StatementTransaction("10/12/2020", "Pagamento de Boleto - BB ADMINISTRADORA DE CARTOES", -402.35, "7"));
        transactions.add(new StatementTransaction("31/12/2020", "S A L D O", 4878.93, "8"));

        var owner = new Owner();
        owner.setId("maria");
        var bank = new Bank();
        bank.setId("BB");
        try {
            Map<String, Double> soma = statementService.categorize(owner, bank, transactions);

            Assertions.assertEquals(-300.80, soma.get("HOME"), 0);
            Assertions.assertEquals(-80.46, soma.get("BEACHHOUSE"), 0);
            Assertions.assertEquals(-101.02, soma.get("SUPERMARKET"), 0);
            Assertions.assertEquals(-101.00, soma.get("PERSONAL"), 0);
            Assertions.assertEquals(-402.35, soma.get("CREDITCARD"), 0);
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }

    }

}
