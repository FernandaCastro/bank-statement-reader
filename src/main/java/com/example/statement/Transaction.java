package com.example.statement;

import org.springframework.format.annotation.NumberFormat;

public class Transaction{


    /*"Data","Dependencia Origem","HistÛrico","Data do Balancete","N˙mero do documento","Valor",
      "30/11/2020","","Saldo Anterior","","0","1814.10",
      "01/12/2020","","Pgto conta ·gua - CEDAE FIDC","","120101","-123.39",
      "03/12/2020","","BenefÌcio INSS","","196324558","4394.95",
    */

    //@CsvBindByName(column = "data")
    private String date;

    //@CsvBindByName(column = "histórico")
    private String description;

    //@CsvBindByName(column = "valor")
    @NumberFormat(pattern = "#.##")
    private double value;

    //@CsvBindByName(column = "número do documento")
    private String id;

    private String category;

    public Transaction(){}

    public Transaction(String date, String description, Double value, String id){
            this.date = date;
            this.description = description;
            this.id = id;
            this.value = value;
    }

    public String getCategory() { return category; }

    public String getDate() { return date; }

    public String getDescription() { return description; }

    public double getValue() { return value; }

    public String getId() { return id; }

    public void setCategory(String category) { this.category = category; }

    public void setDate(String date) { this.date = date; }

    public void setDescription(String description) { this.description = description; }

    public void setValue(double value) { this.value = value; }

    public void setId(String id) { this.id = id; }
}
