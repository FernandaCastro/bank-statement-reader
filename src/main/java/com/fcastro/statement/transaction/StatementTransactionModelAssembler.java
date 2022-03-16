package com.fcastro.statement.transaction;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
class StatementTransactionModelAssembler implements RepresentationModelAssembler<StatementTransaction, EntityModel<StatementTransaction>> {

    @Override
    public EntityModel<StatementTransaction> toModel(StatementTransaction object) {

        return EntityModel.of(object,
                linkTo(methodOn(StatementTransactionController.class).one(object.getStatementId(), object.getId())).withSelfRel(),
                linkTo(methodOn(StatementTransactionController.class).all(object.getStatementId())).withRel("transactions"));
    }
}
