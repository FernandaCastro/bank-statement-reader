package com.fcastro.statementconfig.category;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class StatementConfigCategoryModelAssembler extends RepresentationModelAssemblerSupport<StatementConfigCategory, StatementConfigCategoryModel> {

    public StatementConfigCategoryModelAssembler() {
        super(StatementConfigCategoryController.class, StatementConfigCategoryModel.class);
    }

    @Override
    public StatementConfigCategoryModel toModel(StatementConfigCategory entity)
    {
        StatementConfigCategoryModel model = instantiateModel(entity);

        model.add(linkTo(
                methodOn(StatementConfigCategoryController.class).one(entity.getStatementConfig().getId(), entity.getId()))
                .withSelfRel());

        model.setId(entity.getId());
        model.setName(entity.getName());
        model.setTags(entity.getTags());
        return model;
    }

    @Override
    public CollectionModel<StatementConfigCategoryModel> toCollectionModel(Iterable<? extends StatementConfigCategory> entities)
    {
        CollectionModel<StatementConfigCategoryModel> models = super.toCollectionModel(entities);

        Long configId = null;
        if (entities.iterator().hasNext()) {
            configId = entities.iterator().next().getStatementConfig().getId();
        }

        models.add(linkTo(methodOn(StatementConfigCategoryController.class).all(configId)).withSelfRel());

        return models;
    }
}
