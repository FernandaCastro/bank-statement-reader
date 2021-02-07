package com.example.bank;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.BufferedInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest(classes = BankApplication.class)
public class TransactionServiceTest {

    @Autowired
    private BankStatementService bankStatementService;

    @Test
    public void testRead(){
        String content  = "\"Data\",\"Dependencia Origem\",\"HISTÓRICO\",\"Data do Balancete\",\"Número do documento\",\"Valor\",\n" +
                          "\"28/10/2020\",\"\",\"Saldo Anterior\",\"\",\"0\",\"1750.31\"," ;

        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "statement.csv",
                MediaType.TEXT_PLAIN_VALUE,
                content.getBytes(StandardCharsets.ISO_8859_1)
        );

        try {
            List<Transaction> transactions = bankStatementService.read(file);

            Assertions.assertNotNull(transactions);
            Assertions.assertEquals(1, transactions.size());
            Assertions.assertEquals("28/10/2020", transactions.get(0).getDate());
            Assertions.assertEquals("Saldo Anterior", transactions.get(0).getDescription());
            Assertions.assertEquals(1750.31, transactions.get(0).getValue());

        }catch(Exception e){
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testCategorizeRio(){
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction("01/12/2020","Pgto conta água - CEDAE FIDC",-123.39));
        transactions.add(new Transaction("07/12/2020","Tarifa Pacote de Serviços - Cobrança referente 07/12/2020",-21.20));
        transactions.add(new Transaction("07/12/2020","BB Seguro Auto - SEGURO AUTO BB/MAPFRE",-300.80));
        transactions.add(new Transaction("10/12/2020","Pagto conta telefone - TELEMAR RJ (OI FIXO)",-87.86));
        transactions.add(new Transaction("10/12/2020","Pagamento conta luz - LIGHT",-90.59));
        transactions.add(new Transaction("10/12/2020","Pagamento conta luz - LIGHT", -13.93));
        transactions.add(new Transaction("14/12/2020","Pagto conta telefone - VIVO RJ",-173.45));

        double soma = bankStatementService.categorizeRio(transactions);
        Assertions.assertEquals(-811.22, soma, 0);
    }

    @Test
    public void testCategorizeSaquarema(){
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction("10/12/2020","Pagamento de Boleto - OK NEWS PROVEDOR E COMERCIO DE PRODUTO",-80.12));
        transactions.add(new Transaction("15/12/2020","Pgto conta água - CONC. AGUAS DE JUTURNAIBA", -20.40));
        transactions.add(new Transaction("21/12/2020","Emissão de DOC - 341 4848 49075624700 JORGE LU 006/006M", -150.00));

        double soma = bankStatementService.categorizeSaquarema(transactions);
        Assertions.assertEquals(-250.52, soma, 0);
    }

    @Test
    public void testCategorizeMercado() {
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction("11/12/2020", "Compra com Cartão - 11/12 11:26 SUPERMERCADO ESPERAN", -132.90));
        transactions.add(new Transaction("15/12/2020", "Compra com Cartão - 15/12 11:32 PRINCESA SUPERMERCAD", -145.62));
        transactions.add(new Transaction("30/12/2020", "Compra com Cartão - 30/12 15:11 PRINCESA SUPERMERCAD", -370.66));
        transactions.add(new Transaction("31/12/2020", "Compra com Cartão - 31/12 12:29 SUPERMERCADO ESPERAN", -101.02));

        double soma = bankStatementService.categorizeSupermarket(transactions);
        Assertions.assertEquals(-750.20, soma, 0);
    }

    @Test
    public void testCategorizePessoal(){
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction("21/12/2020","Compra com Cartão - 19/12 12:36 DRICA MODA INTIMA LT", -101.00));

        double soma = bankStatementService.categorizePersonal(transactions);
        Assertions.assertEquals(-101.00, soma, 0);
    }

    @Test
    public void testCategorizeCartao() {
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction("10/12/2020","Pagamento de Boleto - BB ADMINISTRADORA DE CARTOES",-402.35));

        double soma = bankStatementService.categorizeCreditCard(transactions);
        Assertions.assertEquals(-402.35, soma, 0);
    }

    @Test
    public void testCategorizeMix(){
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction("30/11/2020","Saldo Anterior", 1814.10));
        transactions.add(new Transaction("03/12/2020","Benefício INSS", 4394.95));
        transactions.add(new Transaction("04/12/2020","TED-Crédito em Conta - 033 3017 39545900000150 HOT ITAIPU ALI",1045.01));
        transactions.add(new Transaction("07/12/2020","BB Seguro Auto - SEGURO AUTO BB/MAPFRE",-300.80));
        transactions.add(new Transaction("21/12/2020","Compra com Cartão - 19/12 12:36 DRICA MODA INTIMA LT",-101.00));
        transactions.add(new Transaction("15/12/2020","Pgto conta água - CONC. AGUAS DE JUTURNAIBA", -80.46));
        transactions.add(new Transaction("31/12/2020", "Compra com Cartão - 31/12 12:29 SUPERMERCADO ESPERAN", -101.02));
        transactions.add(new Transaction("10/12/2020","Pagamento de Boleto - BB ADMINISTRADORA DE CARTOES", -402.35));
        transactions.add(new Transaction("31/12/2020","S A L D O", 4878.93));

        double somaRio = bankStatementService.categorizeRio(transactions);
        double somaSaqua = bankStatementService.categorizeSaquarema(transactions);
        double somaMercado = bankStatementService.categorizeSupermarket(transactions);
        double somaPessoal = bankStatementService.categorizePersonal(transactions);
        double somaCartao = bankStatementService.categorizeCreditCard(transactions);

        Assert.assertEquals(-300.80, somaRio, 0);
        Assert.assertEquals(-80.46, somaSaqua, 0);
        Assert.assertEquals(-101.02, somaMercado, 0);
        Assert.assertEquals(-101.00, somaPessoal, 0);
        Assert.assertEquals(-402.35, somaCartao, 0);
    }

}
