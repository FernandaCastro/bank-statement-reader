package com.example.bank;

import com.opencsv.bean.CsvBindByName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.stereotype.Component;

@Component
public class AldaTransaction extends Transaction{

    /*"Data","Dependencia Origem","HistÛrico","Data do Balancete","N˙mero do documento","Valor",
      "30/11/2020","","Saldo Anterior","","0","1814.10",
      "01/12/2020","","Pgto conta ·gua - CEDAE FIDC","","120101","-123.39",
      "03/12/2020","","BenefÌcio INSS","","196324558","4394.95",
    */

    @CsvBindByName(column = "Data")
    private String date;

    @CsvBindByName(column = "Histórico")
    private String description;

    @CsvBindByName(column = "Valor")
    @NumberFormat(pattern = "#.##")
    private double value;

    @CsvBindByName(column = "Número do documento")
    private String id;

    public AldaTransaction(){
        super();
    }

    public AldaTransaction(String date, String description, double value, String id){
            this.date = date;
            this.description = description;
            this.value = value;
            this.id = id;
    }

    public String getDate() { return date; }

    public String getDescription() { return description; }

    public double getValue() { return value; }

    public String getId() { return id; }

    public void setDate(String date) { this.date = date; }

    public void setDescription(String description) { this.description = description; }

    public void setValue(double value) { this.value = value; }

    public void setId(String id) { this.id = id; }

}
