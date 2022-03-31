package com.fcastro.client;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
class ClientViewAssembler implements RepresentationModelAssembler<ClientView, EntityModel<ClientView>> {

    @Override
    public EntityModel<ClientView> toModel(ClientView object) {

        return EntityModel.of(object,
                linkTo(methodOn(ClientController.class).one(object.getId())).withSelfRel(),
                linkTo(methodOn(ClientController.class).all()).withRel("clients"));
    }
}
