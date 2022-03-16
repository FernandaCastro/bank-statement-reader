package com.fcastro.statement.transaction;

import com.fcastro.exception.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@AllArgsConstructor
public class StatementTransactionController {

    private static final Logger log = LoggerFactory.getLogger(StatementTransactionController.class);

    private final StatementTransactionRepository repository;
    private final StatementTransactionModelAssembler assembler;

    @GetMapping("api/v1/statements/{statementId}/transactions/{id}")
    EntityModel<StatementTransaction> one(@PathVariable Long statementId, @PathVariable Long id) {

        StatementTransaction resource = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(StatementTransaction.class, id));

        return assembler.toModel(resource);
    }

    @GetMapping("api/v1/statements/{id}/transactions")
    CollectionModel<EntityModel<StatementTransaction>> all(@PathVariable Long id) {

        List<EntityModel<StatementTransaction>> resources = repository.findAllByStatementId(id).stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(resources, linkTo(methodOn(StatementTransactionController.class).all(id)).withSelfRel());
    }
}

