package com.fcastro.statement.transaction;

import com.fcastro.exception.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("api/v1/statements/{statementId}/transactions")
@AllArgsConstructor
public class StatementTransactionController {

    private final StatementTransactionRepository repository;
    private final StatementTransactionModelAssembler assembler;
    private final ModelMapper modelMapper;


    @GetMapping("/{id}")
    public EntityModel<StatementTransactionDto> one(@PathVariable Long statementId, @PathVariable Long id) {

        StatementTransaction resource = repository.findByIdAndStatementId(statementId, id)
                .orElseThrow(() -> new ResourceNotFoundException(StatementTransaction.class, id));

        return assembler.toModel(convertToDTO(resource));
    }

    @GetMapping
    public CollectionModel<EntityModel<StatementTransactionDto>> all(@PathVariable Long id) {

        List<EntityModel<StatementTransactionDto>> resources = repository.findAllByStatementId(id).stream()
                .map(resource -> {
                    return assembler.toModel(convertToDTO(resource));
                })
                .collect(Collectors.toList());

        return CollectionModel.of(resources, linkTo(methodOn(StatementTransactionController.class).all(id)).withSelfRel());
    }

    private StatementTransactionDto convertToDTO(StatementTransaction obj){
        return modelMapper.map(obj, StatementTransactionDto.class);
    }
}

