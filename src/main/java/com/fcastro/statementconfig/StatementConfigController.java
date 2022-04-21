package com.fcastro.statementconfig;

import com.fcastro.client.Client;
import com.fcastro.client.ClientRepository;
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
@RequestMapping("api/v1/configurations")
@AllArgsConstructor
public class StatementConfigController {

    private final StatementConfigRepository statementConfigRepository;
    private final ClientRepository clientRepository;
    private final StatementConfigModelAssembler configAssembler;
    private final ModelMapper modelMapper;

    @GetMapping
    CollectionModel<EntityModel<StatementConfigModel>> all(@RequestParam(required = true) long clientId) {

        clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException(Client.class, clientId));

        List<EntityModel<StatementConfigModel>> configs = statementConfigRepository.findAllByClientId(clientId).stream()
                .map(resource -> {
                    return configAssembler.toModel(convertToDTO(resource));
                })
                .collect(Collectors.toList());

        return CollectionModel.of(configs, linkTo(methodOn(StatementConfigController.class).all(clientId)).withSelfRel());
    }

    @GetMapping("/{id}")
    EntityModel<StatementConfigModel> one(@PathVariable Long id) {

        StatementConfig config = statementConfigRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(StatementConfig.class, id));

        return configAssembler.toModel(convertToDTO(config));
    }

    @PostMapping
    ResponseEntity<?> newConfig(@RequestBody StatementConfigModel newObj) {

        StatementConfig statementConfig = statementConfigRepository.save(convertToObject(newObj));

        EntityModel<StatementConfigModel> entityModel =  configAssembler.toModel(convertToDTO(statementConfig));

        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @PutMapping("/{id}")
    ResponseEntity<?> replace(@RequestBody StatementConfig newObj, @PathVariable Long id) {

        StatementConfig updatedResource = statementConfigRepository.findById(id)
                .map(resource -> {
                    resource.setDescriptionField(newObj.getDescriptionField());
                    resource.setDocumentIdField(newObj.getDocumentIdField());
                    resource.setTransactionDateField(newObj.getTransactionDateField());
                    resource.setTransactionValueField(newObj.getTransactionValueField());
                    return statementConfigRepository.save(resource);
                })
                .orElseGet(() -> {
                    newObj.setId(id);
                    return statementConfigRepository.save(newObj);
                });

        EntityModel<StatementConfigModel> entityModel = configAssembler.toModel(convertToDTO(updatedResource));

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> delete(@PathVariable Long id) {
        statementConfigRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    private StatementConfigModel convertToDTO(StatementConfig obj){
        return modelMapper.map(obj, StatementConfigModel.class);
    }

    private StatementConfig convertToObject(StatementConfigModel obj){
        return modelMapper.map(obj, StatementConfig.class);
    }
}
