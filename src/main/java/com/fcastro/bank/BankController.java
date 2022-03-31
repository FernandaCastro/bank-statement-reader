package com.fcastro.bank;

import com.fcastro.exception.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
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
@RequestMapping("api/v1/banks")
@AllArgsConstructor
public class BankController {

    private final BankRepository repository;
    private final BankViewAssembler assembler;
    private final ModelMapper modelMapper;

    @GetMapping
    CollectionModel<EntityModel<BankView>> all() {

        List<EntityModel<BankView>> banks = repository.findAll().stream()
                .map(bank -> assembler.toModel(convertToModel(bank)))
                .collect(Collectors.toList());

        return CollectionModel.of(banks, linkTo(methodOn(BankController.class).all()).withSelfRel());
    }

    @GetMapping("/{id}")
    public EntityModel<BankView> one(@PathVariable Long id) {

        Bank bank = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Bank.class, id));

        return assembler.toModel(convertToModel(bank));
    }

    @PostMapping
    ResponseEntity<?> newBank(@RequestBody BankView newBank) {

        Bank savedBank = repository.save(convertToObject(newBank));
        EntityModel<BankView> entityModel =  assembler.toModel(convertToModel(savedBank));

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @PutMapping("/{id}")
    ResponseEntity<?> replace(@RequestBody BankView newBank, @PathVariable Long id) {

        Bank updatedBank = repository.findById(id)
                .map(resource -> {
                    resource.setName(newBank.getName());
                    return repository.save(resource);
                })
                .orElseGet(() -> {
                    newBank.setId(id);
                    return repository.save(convertToObject(newBank));
                });

        EntityModel<BankView> entityModel = assembler.toModel(convertToModel(updatedBank));

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> delete(@PathVariable Long id) {

        repository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    private BankView convertToModel(Bank obj){
        return modelMapper.map(obj, BankView.class);
    }

    private Bank convertToObject(BankView obj){
        return modelMapper.map(obj, Bank.class);
    }
}
