package com.fcastro.bank;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
class BankViewAssembler implements RepresentationModelAssembler<BankView, EntityModel<BankView>> {

    @Override
    public EntityModel<BankView> toModel(BankView object) {

        return EntityModel.of(object,
                linkTo(methodOn(BankController.class).one(object.getId())).withSelfRel());
    }
}
