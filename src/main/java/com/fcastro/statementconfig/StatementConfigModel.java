package com.fcastro.statementconfig;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fcastro.statementconfig.category.StatementConfigCategoryModel;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.util.List;

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

    private String descriptionField;
    private String documentIdField;
    private String transactionDateField;
    private String transactionValueField;

    private List<StatementConfigCategoryModel> categories;
}
