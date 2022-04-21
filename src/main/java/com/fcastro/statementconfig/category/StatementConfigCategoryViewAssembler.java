package com.fcastro.statementconfig.category;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class StatementConfigCategoryViewAssembler implements RepresentationModelAssembler<StatementConfigCategoryView, EntityModel<StatementConfigCategoryView>> {

    @Override
    public EntityModel<StatementConfigCategoryView> toModel(StatementConfigCategoryView object) {

        return EntityModel.of(object,
                linkTo(methodOn(StatementConfigCategoryController.class).one(object.getStatementConfigId(), object.getId())).withSelfRel(),
                linkTo(methodOn(StatementConfigCategoryController.class).all(object.getStatementConfigId())).withRel("categories"));
    }
}
