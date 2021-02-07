package com.example.bank;

import com.opencsv.bean.CsvBindByName;
import org.springframework.format.annotation.NumberFormat;

public class Transaction {

    /*"Data","Dependencia Origem","HistÛrico","Data do Balancete","N˙mero do documento","Valor",
      "30/11/2020","","Saldo Anterior","","0","1814.10",
      "01/12/2020","","Pgto conta ·gua - CEDAE FIDC","","120101","-123.39",
      "03/12/2020","","BenefÌcio INSS","","196324558","4394.95",
    */

    @CsvBindByName(column="DATA")
    private String date;

    @CsvBindByName(column = "HISTÓRICO")
    private String description;

    @CsvBindByName(column = "VALOR")
    @NumberFormat(pattern = "#.##")
    private double value;

    private String category;

    public Transaction(String date, String description, double value) {
        this.date = date;
        this.description = description;
        this.value = value;
    }

    public Transaction(){}

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public double getValue() {
        return value;
    }

    public String getCategory() { return category; }

    public void setDate(String date) { this.date = date; }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void setCategory(String category) { this.category = category; }
}
