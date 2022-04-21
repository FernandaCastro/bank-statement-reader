package com.fcastro.bank;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
class BankModelAssembler extends RepresentationModelAssemblerSupport<Bank, BankModel> {

    public BankModelAssembler() {
        super(BankController.class, BankModel.class);
    }

    @Override
    public BankModel toModel(Bank entity)
    {
        BankModel model = instantiateModel(entity);

        model.add(linkTo(
                methodOn(BankController.class).one(entity.getId()))
                .withSelfRel());

        model.setId(entity.getId());
        model.setName(entity.getName());
        return model;
    }

    @Override
    public CollectionModel<BankModel> toCollectionModel(Iterable<? extends Bank> entities)
    {
        CollectionModel<BankModel> models = super.toCollectionModel(entities);

        models.add(linkTo(methodOn(BankController.class).all()).withSelfRel());

        return models;
    }
}
