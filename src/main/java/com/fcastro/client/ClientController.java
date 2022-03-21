package com.fcastro.client;

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
@RequestMapping("api/v1/clients")
@AllArgsConstructor
public class ClientController {

    private final ClientRepository repository;
    private final ClientModelAssembler assembler;
    private final ModelMapper modelMapper;

    @GetMapping
    CollectionModel<EntityModel<ClientDto>> all() {

        List<EntityModel<ClientDto>> resources = repository.findAll().stream()
                .map(resource ->{
                    return assembler.toModel(convertToDTO(resource));
                })
                .collect(Collectors.toList());

        return CollectionModel.of(resources, linkTo(methodOn(ClientController.class).all()).withSelfRel());
    }

    @GetMapping("/{id}")
    EntityModel<ClientDto> one(@PathVariable Long id) {

        Client resource = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Client.class, id));

        return assembler.toModel(convertToDTO(resource));
    }

    @PostMapping
    ResponseEntity<?> newResource(@RequestBody ClientDto newObj) {

        Client client = repository.save(convertToObject(newObj));
        EntityModel<ClientDto> entityModel =  assembler.toModel(convertToDTO(client));

        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @PutMapping("/{id}")
    ResponseEntity<?> replace(@RequestBody ClientDto newObj, @PathVariable Long id) {

        Client updatedResource = repository.findById(id)
                .map(resource -> {
                    resource.setName(newObj.getName());
                    return repository.save(resource);
                })
                .orElseGet(() -> {
                    newObj.setId(id);
                    return repository.save(convertToObject(newObj));
                });

        EntityModel<ClientDto> entityModel = assembler.toModel(convertToDTO(updatedResource));

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> delete(@PathVariable Long id) {
        repository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    private ClientDto convertToDTO(Client obj){
        return modelMapper.map(obj, ClientDto.class);
    }

    private Client convertToObject(ClientDto obj){
        return modelMapper.map(obj, Client.class);
    }
}
