package com.fcastro.statement.transaction;

import lombok.*;
import org.springframework.format.annotation.NumberFormat;

import javax.persistence.*;

@Entity
@Table(name = "STATEMENT_TRANSACTION")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatementTransaction {

    @Id
    @GeneratedValue (strategy= GenerationType.IDENTITY)
    private long id;

    private long statementId;

    //@Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private String transactionDate;

    @Column(nullable = false)
    private String description;

    @NumberFormat(pattern = "#.##")
    @Column(nullable = false)
    private double transactionValue;

    private String documentId;

    private String category;
}
