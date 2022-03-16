package com.fcastro.statement;

import com.fcastro.bank.Bank;
import com.fcastro.bank.BankRepository;
import com.fcastro.client.Client;
import com.fcastro.client.ClientRepository;
import com.fcastro.exception.ResourceNotFoundException;
import com.fcastro.statement.transaction.StatementTransactionModelAssembler;
import com.fcastro.statement.transaction.StatementTransactionRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@AllArgsConstructor
public class StatementController {

    private static final Logger log = LoggerFactory.getLogger(StatementController.class);

    private final ClientRepository clientRepository;
    private final BankRepository bankRepository;
    private final StatementRepository statementRepository;
    private final StatementTransactionRepository statementTransactionRepository;
    private final StatementModelAssembler assembler;
    private final StatementTransactionModelAssembler transactionAssembler;
    private final StatementUploadResponseModelAssembler uploadResponseAssembler;
    private final StatementService statementService;

    @GetMapping("api/v1/statements")
    CollectionModel<EntityModel<Statement>> all() {

        List<EntityModel<Statement>> resources = statementRepository.findAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(resources, linkTo(methodOn(StatementController.class).all()).withSelfRel());
    }

    @GetMapping("api/v1/statements/{id}")
    EntityModel<Statement> one(@PathVariable Long id) {

        Statement resource = statementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Statement.class, id));

        return assembler.toModel(resource);
    }

    @DeleteMapping("api/v1/statements/{id}")
    ResponseEntity<?> delete(@PathVariable Long id) {

        statementService.delete(id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "api/v1/statements/upload",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    ResponseEntity<?> upload(StatementUploadRequest uploadRequest) {

        //TODO validate ClientId, BankId and file
        clientRepository.findById(uploadRequest.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException(Client.class, uploadRequest.getClientId()));

        bankRepository.findById(uploadRequest.getBankId())
                .orElseThrow(() -> new ResourceNotFoundException(Bank.class, uploadRequest.getBankId()));

        if (uploadRequest.getFile().isEmpty()) {
            throw new IllegalStateException("File could not be read.");
        }

        //TODO: Handle processing same file
        StatementUploadResponse uploadResponse = null;

        var transactions = statementService.read(uploadRequest.getClientId(), uploadRequest.getBankId(), uploadRequest.getFile());
        var sumCategories = statementService.categorize(uploadRequest.getClientId(), uploadRequest.getBankId(), transactions);
        Statement statement = statementService.save(uploadRequest.getClientId(), uploadRequest.getBankId(), uploadRequest.getFile().getName(), transactions);

        uploadResponse= StatementUploadResponse.builder()
                        .statement(statement)
                        .transactions(transactions)
                        .summary(sumCategories)
                        .build();

        EntityModel<StatementUploadResponse> entityModel =  uploadResponseAssembler.toModel(uploadResponse);

        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);

    }
}

