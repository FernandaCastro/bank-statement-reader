package com.fcastro.statement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fcastro.statement.transaction.StatementTransactionModel;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonRootName(value = "statement")
@Relation(collectionRelation = "statements", itemRelation = "statement")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StatementModel extends RepresentationModel<StatementModel> {

    private Long id;

    private Instant processedAt;

    private String filename;

    private Long clientId;

    private Long bankId;

    private List<StatementTransactionModel> transactions;

    private Map<String, Double> summary;

}
