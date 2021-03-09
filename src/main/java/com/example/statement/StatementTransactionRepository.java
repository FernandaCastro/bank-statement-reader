package com.example.statement;

import org.springframework.data.repository.CrudRepository;

interface StatementTransactionRepository extends CrudRepository<StatementTransaction, Integer> {
}
