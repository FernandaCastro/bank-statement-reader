package com.fcastro.statement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StatementRepository extends JpaRepository<Statement, Long> {

    List<Statement> findAllByClientId(Long clientId);

    @Query("select s from Statement s where " +
            "s.clientId = :clientId " +
            "and s.bankId = :bankId " +
            "and s.filename = :filename")
    Optional<Statement> findByOwnerIdAndBankIdAndFilename(@Param("clientId") Long clientId,
                                                          @Param("bankId") Long bankId,
                                                          @Param("filename") String filename);

//    @Modifying
//    @Query("update Statement set processedAt = :processedAt where id = :statementId")
//    int updateProcessedAtById(@Param("processedAt") Instant processedAt, @Param("statementId") long statementId);

}
