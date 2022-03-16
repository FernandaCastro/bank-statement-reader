package com.fcastro.statement;

import com.fcastro.statement.transaction.StatementTransactionController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
class StatementUploadResponseModelAssembler implements RepresentationModelAssembler<StatementUploadResponse, EntityModel<StatementUploadResponse>> {

    @Override
    public EntityModel<StatementUploadResponse> toModel(StatementUploadResponse object) {

        return EntityModel.of(object,
                linkTo(methodOn(StatementController.class).one(object.getStatement().getId())).withSelfRel(),
                linkTo(methodOn(StatementTransactionController.class).all(object.getStatement().getId())).withRel("transactions"));
    }
}
