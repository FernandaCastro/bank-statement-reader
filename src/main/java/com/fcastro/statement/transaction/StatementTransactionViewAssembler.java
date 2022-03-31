package com.fcastro.statement.transaction;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class StatementTransactionViewAssembler implements RepresentationModelAssembler<StatementTransactionView, EntityModel<StatementTransactionView>> {

    @Override
    public EntityModel<StatementTransactionView> toModel(StatementTransactionView object) {

        return EntityModel.of(object,
                linkTo(methodOn(StatementTransactionController.class).one(object.getStatementId(), object.getId())).withSelfRel(),
                linkTo(methodOn(StatementTransactionController.class).all(object.getStatementId())).withRel("transactions"));
    }
}
