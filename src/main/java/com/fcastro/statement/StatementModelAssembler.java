package com.fcastro.statement;

import com.fcastro.statement.transaction.StatementTransaction;
import com.fcastro.statement.transaction.StatementTransactionController;
import com.fcastro.statement.transaction.StatementTransactionModel;
import com.fcastro.statementconfig.StatementConfigController;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
class StatementModelAssembler extends RepresentationModelAssemblerSupport<Statement, StatementModel> {

    public StatementModelAssembler() {
        super(StatementConfigController.class, StatementModel.class);
    }

    @Override
    public StatementModel toModel(Statement entity)
    {
        StatementModel model = instantiateModel(entity);

        model.add(linkTo(
                methodOn(StatementController.class).one(entity.getId()))
                .withSelfRel());

        model.setId(entity.getId());
        model.setBankId(entity.getBankId());
        model.setClientId(entity.getClientId());
        model.setTransactions(toCategoryModel(entity.getTransactions()));
        return model;
    }

    @Override
    public CollectionModel<StatementModel> toCollectionModel(Iterable<? extends Statement> entities)
    {
        CollectionModel<StatementModel> models = super.toCollectionModel(entities);

        Long clientId = null;
        if (entities.iterator().hasNext()) {
            clientId = entities.iterator().next().getClientId();
        }

        models.add(linkTo(methodOn(StatementController.class).all(clientId)).withSelfRel());

        return models;
    }

    private List<StatementTransactionModel> toCategoryModel(List<StatementTransaction> transactions) {
        if (transactions == null) {
            return Collections.emptyList();
        }

        return transactions.stream()
                .map(transaction -> StatementTransactionModel.builder()
                        .id(transaction.getId())
                        .description(transaction.getDescription())
                        .documentId(transaction.getDocumentId())
                        .transactionDate(transaction.getTransactionDate())
                        .transactionValue(transaction.getTransactionValue())
                        .category(transaction.getCategory())
                        .build()
                        .add(linkTo(
                                methodOn(StatementTransactionController.class)
                                        .one(transaction.getStatementId(), transaction.getId()))
                                .withSelfRel()))
                .collect(Collectors.toList());
    }
}
