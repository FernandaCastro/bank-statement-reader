package com.fcastro.bank;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
class BankModelAssembler implements RepresentationModelAssembler<BankDto, EntityModel<BankDto>> {

    @Override
    public EntityModel<BankDto> toModel(BankDto object) {

        return EntityModel.of(object,
                linkTo(methodOn(BankController.class).one(object.getId())).withSelfRel(),
                linkTo(methodOn(BankController.class).all()).withRel("banks"));
    }
}
