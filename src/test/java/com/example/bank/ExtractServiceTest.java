package com.example.bank;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest(classes = BankApplication.class)
public class ExtractServiceTest {

    @Autowired
    private ExtractService extractService;

    @Test
    public void testCategorizeRio(){
        List<Extract> extracts = new ArrayList<>();
        extracts.add(new Extract("01/12/2020","Pgto conta água - CEDAE FIDC",-123.39));
        extracts.add(new Extract("07/12/2020","Tarifa Pacote de Serviços - Cobrança referente 07/12/2020",-21.20));
        extracts.add(new Extract("07/12/2020","BB Seguro Auto - SEGURO AUTO BB/MAPFRE",-300.80));
        extracts.add(new Extract("10/12/2020","Pagto conta telefone - TELEMAR RJ (OI FIXO)",-87.86));
        extracts.add(new Extract("10/12/2020","Pagamento conta luz - LIGHT",-90.59));
        extracts.add(new Extract("10/12/2020","Pagamento conta luz - LIGHT", -13.93));
        extracts.add(new Extract("14/12/2020","Pagto conta telefone - VIVO RJ",-173.45));

        double soma = extractService.categorizeRio(extracts);
        Assert.assertEquals(-811.22, soma, 0);
    }

    @Test
    public void testCategorizeSaquarema(){
        List<Extract> extracts = new ArrayList<>();
        extracts.add(new Extract("10/12/2020","Pagamento de Boleto - OK NEWS PROVEDOR E COMERCIO DE PRODUTO",-80.12));
        extracts.add(new Extract("15/12/2020","Pgto conta água - CONC. AGUAS DE JUTURNAIBA", -20.40));
        extracts.add(new Extract("21/12/2020","Emissão de DOC - 341 4848 49075624700 JORGE LU 006/006M", -150.00));

        double soma = extractService.categorizeSaquarema(extracts);
        Assertions.assertEquals(-250.52, soma, 0);
    }

    @Test
    public void testCategorizeMercado() {
        List<Extract> extracts = new ArrayList<>();
        extracts.add(new Extract("11/12/2020", "Compra com Cartão - 11/12 11:26 SUPERMERCADO ESPERAN", -132.90));
        extracts.add(new Extract("15/12/2020", "Compra com Cartão - 15/12 11:32 PRINCESA SUPERMERCAD", -145.62));
        extracts.add(new Extract("30/12/2020", "Compra com Cartão - 30/12 15:11 PRINCESA SUPERMERCAD", -370.66));
        extracts.add(new Extract("31/12/2020", "Compra com Cartão - 31/12 12:29 SUPERMERCADO ESPERAN", -101.02));

        double soma = extractService.categorizeMercado(extracts);
        Assertions.assertEquals(-750.20, soma, 0);
    }

    @Test
    public void testCategorizePessoal(){
        List<Extract> extracts = new ArrayList<>();
        extracts.add(new Extract("21/12/2020","Compra com Cartão - 19/12 12:36 DRICA MODA INTIMA LT", -101.00));

        double soma = extractService.categorizePessoal(extracts);
        Assertions.assertEquals(-101.00, soma, 0);
    }

    @Test
    public void testCategorizeCartao() {
        List<Extract> extracts = new ArrayList<>();
        extracts.add(new Extract("10/12/2020","Pagamento de Boleto - BB ADMINISTRADORA DE CARTOES",-402.35));

        double soma = extractService.categorizeCartao(extracts);
        Assertions.assertEquals(-402.35, soma, 0);
    }

    @Test
    public void testCategorizeMix(){
        List<Extract> extracts = new ArrayList<>();
        extracts.add(new Extract("30/11/2020","Saldo Anterior", 1814.10));
        extracts.add(new Extract("03/12/2020","Benefício INSS", 4394.95));
        extracts.add(new Extract("04/12/2020","TED-Crédito em Conta - 033 3017 39545900000150 HOT ITAIPU ALI",1045.01));
        extracts.add(new Extract("07/12/2020","BB Seguro Auto - SEGURO AUTO BB/MAPFRE",-300.80));
        extracts.add(new Extract("21/12/2020","Compra com Cartão - 19/12 12:36 DRICA MODA INTIMA LT",-101.00));
        extracts.add(new Extract("15/12/2020","Pgto conta água - CONC. AGUAS DE JUTURNAIBA", -80.46));
        extracts.add(new Extract("31/12/2020", "Compra com Cartão - 31/12 12:29 SUPERMERCADO ESPERAN", -101.02));
        extracts.add(new Extract("10/12/2020","Pagamento de Boleto - BB ADMINISTRADORA DE CARTOES", -402.35));
        extracts.add(new Extract("31/12/2020","S A L D O", 4878.93));

        double somaRio = extractService.categorizeRio(extracts);
        double somaSaqua = extractService.categorizeSaquarema(extracts);
        double somaMercado = extractService.categorizeMercado(extracts);
        double somaPessoal = extractService.categorizePessoal(extracts);
        double somaCartao = extractService.categorizeCartao(extracts);

        Assert.assertEquals(-300.80, somaRio, 0);
        Assert.assertEquals(-80.46, somaSaqua, 0);
        Assert.assertEquals(-101.02, somaMercado, 0);
        Assert.assertEquals(-101.00, somaPessoal, 0);
        Assert.assertEquals(-402.35, somaCartao, 0);
    }

}
