package com.fcastro.client;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
class ClientModelAssembler implements RepresentationModelAssembler<ClientDto, EntityModel<ClientDto>> {

    @Override
    public EntityModel<ClientDto> toModel(ClientDto object) {

        return EntityModel.of(object,
                linkTo(methodOn(ClientController.class).one(object.getId())).withSelfRel(),
                linkTo(methodOn(ClientController.class).all()).withRel("clients"));
    }
}
