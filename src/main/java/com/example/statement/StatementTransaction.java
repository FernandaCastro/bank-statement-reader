package com.example.statement;

import org.springframework.format.annotation.NumberFormat;

import javax.persistence.*;

@Table(name = "STATEMENT_TRANSACTION")
@Entity
public class StatementTransaction {

    @Id
    @GeneratedValue (strategy= GenerationType.IDENTITY)
    private int id;

    private String transactionDate;

    private String description;

    @NumberFormat(pattern = "#.##")
    private double transactionValue;

    private String documentId;

    private String category;

    private int statementId;

    public StatementTransaction(){}

    public StatementTransaction(String transactionDate, String description, Double transactionValue, String documentId){
            this.transactionDate = transactionDate;
            this.description = description;
            this.documentId = documentId;
            this.transactionValue = transactionValue;
    }

    public String getCategory() { return category; }

    public String getTransactionDate() { return transactionDate; }

    public String getDescription() { return description; }

    public double getTransactionValue() { return transactionValue; }

    public String getDocumentId() { return documentId; }

    public void setCategory(String category) { this.category = category; }

    public void setTransactionDate(String transactionDate) { this.transactionDate = transactionDate; }

    public void setDescription(String description) { this.description = description; }

    public void setTransactionValue(double transactionValue) { this.transactionValue = transactionValue; }

    public void setDocumentId(String documentId) { this.documentId = documentId; }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public int getStatementId() { return statementId; }

    public void setStatementId(int statementId) { this.statementId = statementId; }

    @Override
    public String toString() {
        return "StatementTransaction{" +
                "id=" + id +
                '}';
    }


}
