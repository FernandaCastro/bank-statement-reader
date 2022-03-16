package com.fcastro.bank;

import com.fcastro.exception.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@AllArgsConstructor
public class BankController {

    private final BankRepository repository;
    private final BankModelAssembler assembler;

    @GetMapping("api/v1/banks")
    CollectionModel<EntityModel<Bank>> all() {

        List<EntityModel<Bank>> banks = repository.findAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(banks, linkTo(methodOn(BankController.class).all()).withSelfRel());
    }

    @GetMapping("api/v1/banks/{id}")
    EntityModel<Bank> one(@PathVariable Long id) {

        Bank bank = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Bank.class, id));

        return assembler.toModel(bank);
    }

    @PostMapping("api/v1/banks")
    ResponseEntity<?> newBank(@RequestBody Bank newObj) {

        EntityModel<Bank> entityModel =  assembler.toModel(repository.save(newObj));

        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @PutMapping("api/v1/banks/{id}")
    ResponseEntity<?> replace(@RequestBody Bank newObj, @PathVariable Long id) {

        Bank updatedResource = repository.findById(id)
                .map(resource -> {
                    resource.setName(newObj.getName());
                    return repository.save(resource);
                })
                .orElseGet(() -> {
                    newObj.setId(id);
                    return repository.save(newObj);
                });

        EntityModel<Bank> entityModel = assembler.toModel(updatedResource);

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @DeleteMapping("api/v1/banks/{id}")
    ResponseEntity<?> delete(@PathVariable Long id) {
        repository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
