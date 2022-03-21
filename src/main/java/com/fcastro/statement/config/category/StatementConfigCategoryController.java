package com.fcastro.statement.config.category;

import com.fcastro.exception.ResourceNotFoundException;
import com.fcastro.statement.config.StatementConfig;
import com.fcastro.statement.config.StatementConfigRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("api/v1/configurations/{statementConfigId}/categories")
@AllArgsConstructor
public class StatementConfigCategoryController {

    private final StatementConfigRepository statementConfigRepository;
    private final StatementConfigCategoryRepository repository;
    private final StatementConfigCategoryModelAssembler assembler;
    private final ModelMapper modelMapper;

    @GetMapping
    CollectionModel<EntityModel<StatementConfigCategoryDto>> all(@PathVariable Long statementConfigId) {

        statementConfigRepository.findById(statementConfigId)
                .orElseThrow(() -> new ResourceNotFoundException(StatementConfig.class, statementConfigId));

        List<EntityModel<StatementConfigCategoryDto>> categories = repository.findAllByStatementConfigId(statementConfigId).stream()
                .map(resource -> {
                    return assembler.toModel(convertToDTO(resource));
                })
                .collect(Collectors.toList());

        return CollectionModel.of(categories, WebMvcLinkBuilder.linkTo(methodOn(StatementConfigCategoryController.class).all(statementConfigId)).withSelfRel());
    }

    @GetMapping("/{id}")
    EntityModel<StatementConfigCategoryDto> one(@PathVariable Long statementConfigId, @PathVariable Long id) {

        statementConfigRepository.findById(statementConfigId)
                .orElseThrow(() -> new ResourceNotFoundException(StatementConfig.class, statementConfigId));

        StatementConfigCategory category = repository.findByStatementConfigIdAndId(statementConfigId, id)
                .orElseThrow(() -> new ResourceNotFoundException(StatementConfigCategory.class, id));

        return assembler.toModel(convertToDTO(category));
    }

    @PostMapping
    ResponseEntity<?> create(@PathVariable Long statementConfigId, @RequestBody StatementConfigCategory newCategory) {

        statementConfigRepository.findById(statementConfigId)
                .orElseThrow(() -> new ResourceNotFoundException(StatementConfig.class, statementConfigId));

        newCategory.setStatementConfigId(statementConfigId);
        StatementConfigCategory category = repository.save(newCategory);

        EntityModel<StatementConfigCategoryDto> entityModel =  assembler.toModel(convertToDTO(category));

        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @PutMapping("/{id}")
    ResponseEntity<?> replace(@PathVariable Long statementConfigId, @PathVariable Long id, @RequestBody StatementConfigCategory newCategory) {

        statementConfigRepository.findById(statementConfigId)
                .orElseThrow(() -> new ResourceNotFoundException(StatementConfig.class, statementConfigId));

        StatementConfigCategory updatedResource = repository.findByStatementConfigIdAndId(statementConfigId, id)
                .map(resource -> {
                    resource.setName(newCategory.getName());
                    resource.setTags(newCategory.getTags());
                    return repository.save(resource);
                })
                .orElseGet(() -> {
                    newCategory.setId(id);
                    return repository.save(newCategory);
                });

        EntityModel<StatementConfigCategoryDto> entityModel = assembler.toModel(convertToDTO(updatedResource));

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> delete(@PathVariable Long statementConfigId, @PathVariable Long id) {
        repository.deleteByStatementConfigIdAndId(statementConfigId, id);

        return ResponseEntity.noContent().build();
    }

    private StatementConfigCategoryDto convertToDTO(StatementConfigCategory obj){
        return modelMapper.map(obj, StatementConfigCategoryDto.class);
    }
}
