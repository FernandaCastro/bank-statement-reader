package com.fcastro.statement.transaction;

import lombok.*;
import org.springframework.format.annotation.NumberFormat;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatementTransactionDto {

    private Long id;

    private Long statementId;

    //@Temporal(TemporalType.DATE)
    private String transactionDate;

    private String description;

    @NumberFormat(pattern = "#.##")
    private Double transactionValue;

    private String documentId;

    private String category;
}
