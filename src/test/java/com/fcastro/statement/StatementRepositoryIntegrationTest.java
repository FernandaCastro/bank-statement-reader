package com.fcastro.statement;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class StatementRepositoryIntegrationTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    StatementRepository statementRepository;

    @Test
    void givenStatement_whenFindByOwnerIdAndBankIdAndFilename_thenReturnsStatement(){
        //given
        entityManager.persistFlushFind(Statement.builder().clientId(1L).bankId(1L).filename("statement.csv").processedAt(Instant.now()).build());

        //when
        Optional<Statement> foundStatement = statementRepository.findByOwnerIdAndBankIdAndFilename(1, 1, "statement.csv");

        //then
        assertThat(foundStatement.get()).isNotNull();
        assertThat(foundStatement.get().getId()).isNotNull();
    }
}
