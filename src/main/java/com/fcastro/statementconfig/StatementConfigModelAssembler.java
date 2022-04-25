package com.fcastro.statementconfig;

import com.fcastro.statementconfig.category.StatementConfigCategory;
import com.fcastro.statementconfig.category.StatementConfigCategoryController;
import com.fcastro.statementconfig.category.StatementConfigCategoryModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
class StatementConfigModelAssembler extends RepresentationModelAssemblerSupport<StatementConfig, StatementConfigModel> {

    public StatementConfigModelAssembler() {
        super(StatementConfigController.class, StatementConfigModel.class);
    }

    @Override
    public StatementConfigModel toModel(StatementConfig entity)
    {
        StatementConfigModel model = instantiateModel(entity);

        model.add(linkTo(
                methodOn(StatementConfigController.class).one(entity.getId()))
                .withSelfRel());

        model.setId(entity.getId());
        model.setBankId(entity.getBankId());
        model.setClientId(entity.getClientId());
        model.setDescriptionField(entity.getDescriptionField());
        model.setDocumentIdField(entity.getDocumentIdField());
        model.setTransactionDateField(entity.getTransactionDateField());
        model.setTransactionValueField(entity.getTransactionValueField());
        model.setCategories(toCategoryModel(entity.getCategories()));
        return model;
    }

    @Override
    public CollectionModel<StatementConfigModel> toCollectionModel(Iterable<? extends StatementConfig> entities)
    {
        CollectionModel<StatementConfigModel> models = super.toCollectionModel(entities);

        Long clientId = null;
        if (entities.iterator().hasNext()) {
            clientId = entities.iterator().next().getClientId();
        }

        models.add(linkTo(methodOn(StatementConfigController.class).all(clientId)).withSelfRel());

        return models;
    }

    private List<StatementConfigCategoryModel> toCategoryModel(List<StatementConfigCategory> categories) {
        if (categories.isEmpty())
            return Collections.emptyList();

        return categories.stream()
                .map(category -> StatementConfigCategoryModel.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .tags(category.getTags())
                        .build()
                        .add(linkTo(
                                methodOn(StatementConfigCategoryController.class)
                                        .one(category.getStatementConfig().getId(), category.getId()))
                                .withSelfRel()))
                .collect(Collectors.toList());
    }
}
