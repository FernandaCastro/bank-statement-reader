package com.fcastro.client;

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
public class ClientController {

    private final ClientRepository repository;
    private final ClientModelAssembler assembler;

    @GetMapping("api/v1/clients")
    CollectionModel<EntityModel<Client>> all() {

        List<EntityModel<Client>> resources = repository.findAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(resources, linkTo(methodOn(ClientController.class).all()).withSelfRel());
    }

    @GetMapping("api/v1/clients/{id}")
    EntityModel<Client> one(@PathVariable Long id) {

        Client resource = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Client.class, id));

        return assembler.toModel(resource);
    }

    @PostMapping("api/v1/clients")
    ResponseEntity<?> newResource(@RequestBody Client newObj) {

        EntityModel<Client> entityModel =  assembler.toModel(repository.save(newObj));

        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @PutMapping("api/v1/clients/{id}")
    ResponseEntity<?> replace(@RequestBody Client newObj, @PathVariable Long id) {

        Client updatedResource = repository.findById(id)
                .map(resource -> {
                    resource.setName(newObj.getName());
                    return repository.save(resource);
                })
                .orElseGet(() -> {
                    newObj.setId(id);
                    return repository.save(newObj);
                });

        EntityModel<Client> entityModel = assembler.toModel(updatedResource);

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @DeleteMapping("api/v1/clients/{id}")
    ResponseEntity<?> delete(@PathVariable Long id) {
        repository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
