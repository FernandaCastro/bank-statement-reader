package com.fcastro.statement;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
class StatementModelAssembler implements RepresentationModelAssembler<Statement, EntityModel<Statement>> {

    @Override
    public EntityModel<Statement> toModel(Statement object) {

        return EntityModel.of(object,
                linkTo(methodOn(StatementController.class).one(object.getId())).withSelfRel(),
                linkTo(methodOn(StatementController.class).all()).withRel("statements"));
    }
}
