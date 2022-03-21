package com.fcastro.statement;

import com.fcastro.statement.transaction.StatementTransaction;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("api/v1/statements")
@AllArgsConstructor
public class StatementController {

    private static final Logger log = LoggerFactory.getLogger(StatementController.class);

    private final StatementModelAssembler assembler;
    private final StatementUploadResponseModelAssembler uploadResponseAssembler;
    private final StatementService service;
    private final ModelMapper modelMapper;

    @GetMapping
    CollectionModel<EntityModel<StatementDto>> all() {

        List<EntityModel<StatementDto>> resources = service.findAll().stream()
                .map(resource -> {
                        return assembler.toModel(convertToDTO(resource));
                })
                .collect(Collectors.toList());

        return CollectionModel.of(resources, linkTo(methodOn(StatementController.class).all()).withSelfRel());
    }

    @GetMapping("/{id}")
    EntityModel<StatementDto> one(@PathVariable Long id) {

        Statement resource = service.findById(id);

        return assembler.toModel(convertToDTO(resource));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> delete(@PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/upload",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    ResponseEntity<?> upload(StatementUploadRequest uploadRequest) {

        service.validate(
                uploadRequest.getClientId(),
                uploadRequest.getBankId(),
                uploadRequest.getFile());

        //TODO: Handle processing same file

        List<StatementTransaction> transactions = service.read(uploadRequest.getClientId(), uploadRequest.getBankId(), uploadRequest.getFile());
        Map<String, Double> sumCategories = service.categorize(uploadRequest.getClientId(), uploadRequest.getBankId(), transactions);
        Statement statement = service.save(uploadRequest.getClientId(), uploadRequest.getBankId(), uploadRequest.getFile().getName(), transactions);

        StatementUploadResponse uploadResponse= StatementUploadResponse.builder()
                        .statement(statement)
                        .transactions(transactions)
                        .summary(sumCategories)
                        .build();

        EntityModel<StatementUploadResponse> entityModel =  uploadResponseAssembler.toModel(uploadResponse);

        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);

    }

    private StatementDto convertToDTO(Statement obj){
        return modelMapper.map(obj, StatementDto.class);
    }
}

