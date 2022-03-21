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
    private final BankModelAssembler assembler;
    private final ModelMapper modelMapper;

    @GetMapping
    CollectionModel<EntityModel<BankDto>> all() {

        List<EntityModel<BankDto>> banks = repository.findAll().stream()
                .map(bank -> {
                    return assembler.toModel(convertToDTO(bank));
                })
                .collect(Collectors.toList());

        return CollectionModel.of(banks, linkTo(methodOn(BankController.class).all()).withSelfRel());
    }

    @GetMapping("/{id}")
    EntityModel<BankDto> one(@PathVariable Long id) {

        Bank bank = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Bank.class, id));

        return assembler.toModel(convertToDTO(bank));
    }

    @PostMapping
    ResponseEntity<?> newBank(@RequestBody Bank newBank) {

        EntityModel<BankDto> entityModel =  assembler.toModel(convertToDTO(repository.save(newBank)));

        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @PutMapping("/{id}")
    ResponseEntity<?> replace(@RequestBody Bank newBank, @PathVariable Long id) {

        Bank updatedBank = repository.findById(id)
                .map(resource -> {
                    resource.setName(newBank.getName());
                    return repository.save(resource);
                })
                .orElseGet(() -> {
                    newBank.setId(id);
                    return repository.save(newBank);
                });

        EntityModel<BankDto> entityModel = assembler.toModel(convertToDTO(updatedBank));

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> delete(@PathVariable Long id) {
        repository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    private BankDto convertToDTO(Bank obj){
        return modelMapper.map(obj, BankDto.class);
    }

    private Bank convertToObject(BankDto obj){
        return modelMapper.map(obj, Bank.class);
    }
}
