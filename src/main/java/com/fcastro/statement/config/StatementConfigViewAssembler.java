package com.fcastro.statement.config;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
class StatementConfigViewAssembler implements RepresentationModelAssembler<StatementConfigView, EntityModel<StatementConfigView>> {

    @Override
    public EntityModel<StatementConfigView> toModel(StatementConfigView object) {

        return EntityModel.of(object,
                linkTo(methodOn(StatementConfigController.class).one(object.getId())).withSelfRel(),
                linkTo(methodOn(StatementConfigController.class).all(object.getClientId())).withRel("configurations"));
    }
}
