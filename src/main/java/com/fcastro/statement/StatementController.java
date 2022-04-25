package com.fcastro.statement;

import com.fcastro.statement.transaction.StatementTransaction;
import lombok.AllArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/statements")
@AllArgsConstructor
public class StatementController {

    private final StatementModelAssembler assembler;
    private final StatementService service;

    @GetMapping
    ResponseEntity<CollectionModel<StatementModel>> all(@RequestParam(required = true) Long clientId) {

        List<Statement> statements = service.findAllByClientId(clientId);

        return new ResponseEntity<>(
                assembler.toCollectionModel(statements),
                HttpStatus.OK);
    }

    @GetMapping("/{id}")
    ResponseEntity<StatementModel> one(@PathVariable Long id) {

        Statement statement = service.findById(id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(assembler.toModel(statement));

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

        List<StatementTransaction> transactions = service.processStatementFile(clientId, bankId, file.getName(), reader);
        Statement statement = service.save(clientId, bankId, file.getName(), transactions);
        Map<String, Double> summary = service.summarizeByCategory(statement);

        StatementModel statementModel = assembler.toModel(statement);
        statementModel.setSummary(summary);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(statementModel);
    }
}

