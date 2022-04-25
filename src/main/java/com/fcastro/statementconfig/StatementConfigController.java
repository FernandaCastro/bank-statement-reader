package com.fcastro.statementconfig;

import com.fcastro.bank.Bank;
import com.fcastro.exception.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/configurations")
@AllArgsConstructor
public class StatementConfigController {

    private final StatementConfigRepository repository;
    private final StatementConfigModelAssembler assembler;
    private final ModelMapper modelMapper;

    @GetMapping
    ResponseEntity<CollectionModel<StatementConfigModel>> all(@RequestParam(required = true) long clientId) {

        List<StatementConfig> configs = repository.findAllByClientId(clientId);

        return new ResponseEntity<>(
                assembler.toCollectionModel(configs),
                HttpStatus.OK);
    }

    @GetMapping("/{id}")
    ResponseEntity<StatementConfigModel> one(@PathVariable Long id) {

        return repository.findById(id)
                .map(assembler::toModel)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(Bank.class, id));
    }

    @PostMapping
    ResponseEntity<?> create(@RequestBody StatementConfigModel newConfig) {

        StatementConfig config = repository.save(convertToObject(newConfig));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(assembler.toModel(config));
    }

    @PutMapping("/{id}")
    ResponseEntity<?> replace(@RequestBody StatementConfigModel newConfig, @PathVariable Long id) {

        StatementConfig updatedConfig = repository.findById(id)
                .map(resource -> {
                    resource.setDescriptionField(newConfig.getDescriptionField());
                    resource.setDocumentIdField(newConfig.getDocumentIdField());
                    resource.setTransactionDateField(newConfig.getTransactionDateField());
                    resource.setTransactionValueField(newConfig.getTransactionValueField());
                    return repository.save(resource);
                })
                .orElseGet(() -> {
                    newConfig.setId(id);
                    return repository.save(convertToObject(newConfig));
                });

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(assembler.toModel(updatedConfig));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> delete(@PathVariable Long id) {
        repository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    private StatementConfig convertToObject(StatementConfigModel obj){
        return modelMapper.map(obj, StatementConfig.class);
    }
}
