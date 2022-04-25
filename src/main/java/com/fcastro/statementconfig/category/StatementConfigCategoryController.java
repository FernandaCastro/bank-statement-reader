package com.fcastro.statementconfig.category;

import com.fcastro.exception.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/configurations/{configId}/categories")
@AllArgsConstructor
public class StatementConfigCategoryController {

    private final StatementConfigCategoryRepository repository;
    private final StatementConfigCategoryModelAssembler assembler;
    private final ModelMapper modelMapper;

    @GetMapping
    public ResponseEntity<CollectionModel<StatementConfigCategoryModel>> all(@PathVariable Long configId) {

        List<StatementConfigCategory> categories = repository.findAllByStatementConfigId(configId);

        return new ResponseEntity<>(
                assembler.toCollectionModel(categories),
                HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StatementConfigCategoryModel> one(@PathVariable Long configId, @PathVariable Long id) {

        return repository.findByStatementConfigIdAndId(configId, id)
                .map(assembler::toModel)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(StatementConfigCategory.class, id));
    }

    @PostMapping
    ResponseEntity<StatementConfigCategoryModel> create(@PathVariable Long configId, @RequestBody StatementConfigCategoryModel newCategory) {

        newCategory.setStatementConfigId(configId);
        StatementConfigCategory category = repository.save(convertToObject(newCategory));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(assembler.toModel(category));
    }

    @PutMapping("/{id}")
    ResponseEntity<StatementConfigCategoryModel> replace(@PathVariable Long configId, @PathVariable Long id, @RequestBody StatementConfigCategoryModel newCategory) {

        StatementConfigCategory updatedCategory = repository.findByStatementConfigIdAndId(configId, id)
                .map(resource -> {
                    resource.setName(newCategory.getName());
                    resource.setTags(newCategory.getTags());
                    return repository.save(resource);
                })
                .orElseGet(() -> {
                    newCategory.setId(id);
                    return repository.save(convertToObject(newCategory));
                });

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(assembler.toModel(updatedCategory));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> delete(@PathVariable Long configId, @PathVariable Long id) {
        repository.deleteByStatementConfigIdAndId(configId, id);

        return ResponseEntity.noContent().build();
    }

    private StatementConfigCategory convertToObject(StatementConfigCategoryModel obj){
        return modelMapper.map(obj, StatementConfigCategory.class);
    }
}
