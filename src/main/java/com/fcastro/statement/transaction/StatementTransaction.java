package com.fcastro.statement.transaction;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.NumberFormat;

import javax.persistence.*;

@Entity
@Table(name = "STATEMENT_TRANSACTION")
@Getter @Setter @NoArgsConstructor
public class StatementTransaction {

    @Id
    @GeneratedValue (strategy= GenerationType.IDENTITY)
    private long id;

    private long statementId;

    //@Temporal(TemporalType.DATE)
    private String transactionDate;

    private String description;

    @NumberFormat(pattern = "#.##")
    private double transactionValue;

    private String documentId;

    private String category;
}
