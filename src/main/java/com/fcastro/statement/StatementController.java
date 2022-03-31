package com.fcastro.statement;

import lombok.AllArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("api/v1/statements")
@AllArgsConstructor
public class StatementController {

    private final StatementViewAssembler assembler;
    private final StatementUploadResponseModelAssembler uploadResponseAssembler;
    private final StatementService service;

    @GetMapping
    CollectionModel<EntityModel<StatementView>> all() {

        List<EntityModel<StatementView>> resources = service.findAll().stream()
                .map(resource -> {
                        return assembler.toModel(service.convertToModel(resource));
                })
                .collect(Collectors.toList());

        return CollectionModel.of(resources, linkTo(methodOn(StatementController.class).all()).withSelfRel());
    }

    @GetMapping("/{id}")
    EntityModel<StatementView> one(@PathVariable Long id) {

        Statement resource = service.findById(id);

        return assembler.toModel(service.convertToModel(resource));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> delete(@PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/upload",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    ResponseEntity<?> upload(@RequestParam Long clientId, @RequestParam Long bankId, @RequestParam MultipartFile file ) {

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(file.getInputStream(), "ISO-8859-1"));
        } catch (IOException e) {
            throw new IllegalStateException("File [" + file.getName() + "] could not be read.");
        }

        StatementUploadResponse uploadResponse = service.uploadStatements(clientId, bankId, file.getName(), reader);

        EntityModel<StatementUploadResponse> entityModel =  uploadResponseAssembler.toModel(uploadResponse);

        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);

    }
}

