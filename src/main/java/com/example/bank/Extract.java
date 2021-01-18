package com.example.bank;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvNumber;
import org.springframework.format.annotation.NumberFormat;

import java.math.BigDecimal;

public class Extract {

    /*"Data","Dependencia Origem","HistÛrico","Data do Balancete","N˙mero do documento","Valor",
      "30/11/2020","","Saldo Anterior","","0","1814.10",
      "01/12/2020","","Pgto conta ·gua - CEDAE FIDC","","120101","-123.39",
      "03/12/2020","","BenefÌcio INSS","","196324558","4394.95",
    */

    @CsvBindByName
    private String data;

    @CsvBindByName(column = "HISTÓRICO")
    private String historico;

    @CsvBindByName
   // @CsvNumber("#.##")
    @NumberFormat(pattern = "#.##")
    private double valor;

    public Extract(String data, String historico, double valor) {
        this.data = data;
        this.historico = historico;
        this.valor = valor;
    }

    public Extract(){}

    public String getData() {
        return data;
    }

    public String getHistorico() {
        return historico;
    }

    public double getValor() {
        return valor;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }
}
