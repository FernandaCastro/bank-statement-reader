package com.fcastro.statement.transaction;

import com.fcastro.statement.Statement;
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
    private Long id;

    private Long statementId;

    //@Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private String transactionDate;

    @Column(nullable = false)
    private String description;

    @NumberFormat(pattern = "#.##")
    @Column(nullable = false)
    private Double transactionValue;

    private String documentId;

    private String category;

    @ManyToOne
    @JoinColumn(name="statementId", insertable = false, updatable = false)
    private Statement statement;
}
