package com.example.bank;

import com.opencsv.bean.CsvBindByName;
import org.springframework.format.annotation.NumberFormat;

public class Transaction {

    private String category;

    public Transaction(){}

    public String getCategory() { return category; }

    public void setCategory(String category) { this.category = category; }
}
