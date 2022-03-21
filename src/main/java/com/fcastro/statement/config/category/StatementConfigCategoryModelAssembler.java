package com.fcastro.statement.config.category;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class StatementConfigCategoryModelAssembler implements RepresentationModelAssembler<StatementConfigCategoryDto, EntityModel<StatementConfigCategoryDto>> {

    @Override
    public EntityModel<StatementConfigCategoryDto> toModel(StatementConfigCategoryDto object) {

        return EntityModel.of(object,
                linkTo(methodOn(StatementConfigCategoryController.class).one(object.getStatementConfigId(), object.getId())).withSelfRel(),
                linkTo(methodOn(StatementConfigCategoryController.class).all(object.getStatementConfigId())).withRel("categories"));
    }
}
