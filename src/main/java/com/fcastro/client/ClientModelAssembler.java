package com.fcastro.client;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
class ClientModelAssembler extends RepresentationModelAssemblerSupport<Client, ClientModel> {

    public ClientModelAssembler() {
        super(ClientController.class, ClientModel.class);
    }

    @Override
    public ClientModel toModel(Client entity)
    {
        ClientModel model = instantiateModel(entity);

        model.add(linkTo(
                methodOn(ClientController.class).one(entity.getId()))
                .withSelfRel());

        model.setId(entity.getId());
        model.setName(entity.getName());
        return model;
    }

    @Override
    public CollectionModel<ClientModel> toCollectionModel(Iterable<? extends Client> entities)
    {
        CollectionModel<ClientModel> models = super.toCollectionModel(entities);

        models.add(linkTo(methodOn(ClientController.class).all()).withSelfRel());

        return models;
    }
}
