package com.fcastro.statement.transaction;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.*;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonRootName(value = "transaction")
@Relation(collectionRelation = "transactions", itemRelation = "transactions")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StatementTransactionModel extends RepresentationModel<StatementTransactionModel> {

    private Long id;

    private Long statementId;

    private String transactionDate;

    private String description;

    @NumberFormat(pattern = "#.##")
    private Double transactionValue;

    private String documentId;

    private String category;
}
