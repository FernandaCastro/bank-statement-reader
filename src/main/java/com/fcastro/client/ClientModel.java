package com.fcastro.client;

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
@JsonRootName(value = "client")
@Relation(collectionRelation = "clients", itemRelation = "client")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClientModel extends RepresentationModel<ClientModel> {

    private Long id;
    private String name;
}
