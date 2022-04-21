package com.fcastro.bank;

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
@JsonRootName(value = "bank")
@Relation(collectionRelation = "banks", itemRelation = "bank")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BankModel extends RepresentationModel<BankModel> {

    private Long id;
    private String name;
}
