package com.fcastro.statement.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StatementTransactionRepository extends JpaRepository<StatementTransaction, Long> {

    @Query("select t from StatementTransaction t where t.statementId = :statementId")
    List<StatementTransaction> findAllByStatementId(@Param("statementId") long statementId);

    @Query("select t from StatementTransaction t where t.id = :id and t.statementId = :statementId")
    Optional<StatementTransaction> findByIdAndStatementId(@Param("id") long id, @Param("statementId") long statementId);

    @Modifying
    @Query("delete from StatementTransaction t where t.statementId = :statementId")
    void deleteAllByStatementId(@Param("statementId") long statementId);
}
