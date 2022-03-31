package com.fcastro.statement.transaction;

import lombok.*;
import org.springframework.format.annotation.NumberFormat;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatementTransactionView {

    private long id;

    private long statementId;

    //@Temporal(TemporalType.DATE)
    private String transactionDate;

    private String description;

    @NumberFormat(pattern = "#.##")
    private Double transactionValue;

    private String documentId;

    private String category;
}
