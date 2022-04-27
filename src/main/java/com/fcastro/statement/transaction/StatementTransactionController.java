package com.fcastro.statement.transaction;

import com.fcastro.exception.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/statements/{statementId}/transactions")
@AllArgsConstructor
public class StatementTransactionController {

    private final StatementTransactionRepository repository;
    private final StatementTransactionModelAssembler assembler;

    @GetMapping("/{id}")
    public ResponseEntity<StatementTransactionModel> one(@PathVariable Long statementId, @PathVariable Long id) {

        StatementTransaction transaction = repository.findByIdAndStatementId(statementId, id)
                .orElseThrow(() -> new ResourceNotFoundException(StatementTransaction.class, id));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(assembler.toModel(transaction));
    }

    @GetMapping
    public ResponseEntity<CollectionModel<StatementTransactionModel>> all(@PathVariable Long statementId, @RequestParam(required = false) MultiValueMap<String, String> allParams) {

        List<StatementTransaction> transactions;

        if (allParams != null && !allParams.isEmpty()){
            //transactionDate, description, transactionValue, documentId, category
            transactions = repository.findAllByStatementIdAndAllParams(statementId, allParams);

        }else{
            transactions = repository.findAllByStatementId(statementId);
        }

        return new ResponseEntity<>(
                assembler.toCollectionModel(transactions),
                HttpStatus.OK);
    }

}

