package com.fcastro.statementconfig.category;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StatementConfigCategoryRepository extends CrudRepository<StatementConfigCategory, Long> {
    List<StatementConfigCategory> findAllByStatementConfigId(long statementConfigId);

    Optional<StatementConfigCategory> findByStatementConfigIdAndId(long configId, long id);

    void deleteByStatementConfigIdAndId(long configId, long id);
}
