package com.fcastro.statement.transaction;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class StatementTransactionModelAssembler extends RepresentationModelAssemblerSupport<StatementTransaction, StatementTransactionModel> {

    public StatementTransactionModelAssembler() {
        super(StatementTransactionController.class, StatementTransactionModel.class);
    }

    @Override
    public StatementTransactionModel toModel(StatementTransaction entity)
    {
        StatementTransactionModel model = instantiateModel(entity);

        model.add(linkTo(
                methodOn(StatementTransactionController.class).one(entity.getStatement().getId(), entity.getId()))
                .withSelfRel());

        model.setId(entity.getId());
        model.setStatementId(entity.getStatementId());
        model.setDescription(entity.getDescription());
        model.setDocumentId(entity.getDocumentId());
        model.setTransactionDate(entity.getTransactionDate());
        model.setTransactionValue(entity.getTransactionValue());
        model.setCategory(entity.getCategory());
        return model;
    }

    @Override
    public CollectionModel<StatementTransactionModel> toCollectionModel(Iterable<? extends StatementTransaction> entities)
    {
        CollectionModel<StatementTransactionModel> models = super.toCollectionModel(entities);

        Long statementId = null;
        if (entities.iterator().hasNext()) {
            statementId = entities.iterator().next().getStatement().getId();
        }

        models.add(linkTo(methodOn(StatementTransactionController.class).all(statementId, null)).withSelfRel());

        return models;
    }
}
