package com.fcastro.bank;

import com.fcastro.exception.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/banks")
@AllArgsConstructor
public class BankController {

    private final BankRepository repository;
    private final BankModelAssembler assembler;
    private final ModelMapper modelMapper;

    @GetMapping
    ResponseEntity<CollectionModel<BankModel>> all() {

        List<Bank> banks = repository.findAll();

        return new ResponseEntity<>(
                assembler.toCollectionModel(banks),
                HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BankModel> one(@PathVariable Long id) {

        return repository.findById(id)
                .map(assembler::toModel)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(Bank.class, id));
    }

    @PostMapping
    ResponseEntity<BankModel> create(@RequestBody BankModel newBank) {

        Bank bank = repository.save(convertToObject(newBank));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(assembler.toModel(bank));
    }

    @PutMapping("/{id}")
    ResponseEntity<?> replace(@RequestBody BankModel newBank, @PathVariable Long id) {

        Bank bank = repository.findById(id)
                .map(resource -> {
                    resource.setName(newBank.getName());
                    return repository.save(resource);
                })
                .orElseGet(() -> {
                    newBank.setId(id);
                    return repository.save(convertToObject(newBank));
                });

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(assembler.toModel(bank));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> delete(@PathVariable Long id) {

        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private Bank convertToObject(BankModel model){
        return modelMapper.map(model, Bank.class);
    }
}
