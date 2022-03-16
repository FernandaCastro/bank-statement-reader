package com.fcastro.statement.config;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatementCategoryConfigRepository extends CrudRepository<StatementCategoryConfig, Long> {
    List<StatementCategoryConfig> findAllByStatementConfigId(Long statementConfigId);
}
