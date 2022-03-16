package com.fcastro.statement.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatementTransactionRepository extends JpaRepository<StatementTransaction, Long> {

    @Query("select t from StatementTransaction t where t.statementId = :statementId")
    List<StatementTransaction> findAllByStatementId(@Param("statementId") long statementId);

    @Query("delete from StatementTransaction t where t.statementId = :statementId")
    void deleteAllByStatementId(@Param("statementId") long statementId);
}
