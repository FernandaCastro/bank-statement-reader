package com.fcastro.statementconfig.category;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonRootName(value = "category")
@Relation(collectionRelation = "categories", itemRelation = "category")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StatementConfigCategoryModel extends RepresentationModel<StatementConfigCategoryModel> {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String tags;

    private Long statementConfigId;
}
