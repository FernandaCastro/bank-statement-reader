package com.fcastro.statementconfig;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonRootName(value = "configuration")
@Relation(collectionRelation = "configurations", itemRelation = "configuration")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StatementConfigModel extends RepresentationModel<StatementConfigModel> {

    private Long id;
    private Long clientId;
    private Long bankId;
    private String[] transactionFields;
}
