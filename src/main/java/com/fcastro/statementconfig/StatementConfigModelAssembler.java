package com.fcastro.statementconfig;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
class StatementConfigModelAssembler implements RepresentationModelAssembler<StatementConfigModel, EntityModel<StatementConfigModel>> {

    @Override
    public EntityModel<StatementConfigModel> toModel(StatementConfigModel object) {

        return EntityModel.of(object,
                linkTo(methodOn(StatementConfigController.class).one(object.getId())).withSelfRel(),
                linkTo(methodOn(StatementConfigController.class).all(object.getClientId())).withRel("configurations"));
    }
}
