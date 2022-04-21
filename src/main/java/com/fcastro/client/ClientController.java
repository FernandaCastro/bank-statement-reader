package com.fcastro.client;

import com.fcastro.exception.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/clients")
@AllArgsConstructor
public class ClientController {

    private final ClientRepository repository;
    private final ClientModelAssembler assembler;
    private final ModelMapper modelMapper;

    @GetMapping
    ResponseEntity<CollectionModel<ClientModel>> all() {

        List<Client> clients = repository.findAll();

        return new ResponseEntity<>(
                assembler.toCollectionModel(clients),
                HttpStatus.OK);
    }

    @GetMapping("/{id}")
    ResponseEntity<ClientModel> one(@PathVariable Long id) {

        return repository.findById(id)
                .map(assembler::toModel)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(Client.class, id));
    }

    @PostMapping
    ResponseEntity<?> create(@RequestBody ClientModel newClient) {

        Client client = repository.save(convertToObject(newClient));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(assembler.toModel(client));
    }

    @PutMapping("/{id}")
    ResponseEntity<?> replace(@RequestBody ClientModel newClient, @PathVariable Long id) {

        Client client = repository.findById(id)
                .map(resource -> {
                    resource.setName(newClient.getName());
                    return repository.save(resource);
                })
                .orElseGet(() -> {
                    newClient.setId(id);
                    return repository.save(convertToObject(newClient));
                });

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(assembler.toModel(client));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> delete(@PathVariable Long id) {

        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private Client convertToObject(ClientModel obj){
        return modelMapper.map(obj, Client.class);
    }
}
