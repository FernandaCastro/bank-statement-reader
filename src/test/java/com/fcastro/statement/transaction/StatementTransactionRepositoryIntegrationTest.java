package com.fcastro.statement.transaction;

import com.fcastro.statement.Statement;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class StatementTransactionRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private StatementTransactionRepository statementTransactionRepository;

    @Test
    void whenFindAllByStatementId_thenReturnsStatementTransactionList(){
        //given
        Statement statement = entityManager.persistFlushFind(Statement.builder().clientId(1L).bankId(1L).filename("statement.csv").processedAt(Instant.now()).build());
        StatementTransaction transaction1 = StatementTransaction.builder().statementId(statement.getId()).transactionDate("01/01/2022").transactionValue(1.00).description("Foo1").build();
        StatementTransaction transaction2 = StatementTransaction.builder().statementId(statement.getId()).transactionDate("02/01/2022").transactionValue(2.00).description("Foo2").build();
        Arrays.asList(transaction1, transaction2).forEach(entityManager::persistAndFlush);

        //when
        List<StatementTransaction> transactions = statementTransactionRepository.findAllByStatementId(1);

        //then
        assertThat(transactions).isNotNull();
        assertThat(transactions).hasSize(2);
    }

    @Test
    void whenFindByIdAndStatementId_thenReturnsStatementTransaction(){
        //given
        Statement statement = entityManager.persistFlushFind(Statement.builder().clientId(1L).bankId(1L).filename("statement.csv").processedAt(Instant.now()).build());
        StatementTransaction transaction = entityManager.persistFlushFind(StatementTransaction.builder().statementId(statement.getId()).transactionDate("03/01/2022").transactionValue(1.00).description("Foo3").build());

        //when
        Optional<StatementTransaction> foundTransaction = statementTransactionRepository.findByIdAndStatementId(transaction.getId(), statement.getId());

        //then
        assertThat(foundTransaction.get()).isNotNull();
        assertThat(foundTransaction.get().getId()).isEqualTo(transaction.getId());
        assertThat(foundTransaction.get().getStatementId()).isEqualTo(statement.getId());
    }

    @Test
    void whenDeleteAllByStatementId_thenDeletesStatementTransactions(){
        //given
        Statement statement = entityManager.persistFlushFind(Statement.builder().clientId(1L).bankId(1L).filename("statement.csv").processedAt(Instant.now()).build());
        StatementTransaction transaction1 = StatementTransaction.builder().statementId(statement.getId()).transactionDate("04/01/2022").transactionValue(1.00).description("Foo4").build();
        StatementTransaction transaction2 = StatementTransaction.builder().statementId(statement.getId()).transactionDate("05/01/2022").transactionValue(2.00).description("Foo5").build();
        Arrays.asList(transaction1, transaction2).forEach(entityManager::persistAndFlush);
        Long totalBeforeDeletion = (Long) entityManager.getEntityManager().createQuery("SELECT count(t) from StatementTransaction t where t.statementId = ?1").setParameter(1, statement.getId()).getSingleResult();

        //when
        statementTransactionRepository.deleteAllByStatementId(statement.getId());

        //then
        Long totalAfterDeletion = (Long) entityManager.getEntityManager().createQuery("SELECT count(t) from StatementTransaction t where t.statementId = ?1").setParameter(1, statement.getId()).getSingleResult();
        assertThat(totalBeforeDeletion).isEqualTo(2);
        assertThat(totalAfterDeletion).isEqualTo(0);
    }

    @Test
    void findAllByStatementIdAndAllParams_thenReturnsStatementTransactionList(){
        //given
        Statement statement = entityManager.persistFlushFind(Statement.builder().clientId(1L).bankId(1L).filename("statement.csv").processedAt(Instant.now()).build());
        StatementTransaction transaction1 = StatementTransaction.builder().statementId(statement.getId())
                .transactionDate("01/01/2022")
                .transactionValue(1.00)
                .description("Foo1")
                .documentId("DOC1")
                .category("home").build();

        StatementTransaction transaction2= StatementTransaction.builder().statementId(statement.getId())
                .transactionDate("01/01/2022")
                .transactionValue(1.00)
                .description("Foo1.5")
                .documentId("DOC1.5")
                .category("home").build();

        StatementTransaction transaction3 = StatementTransaction.builder().statementId(statement.getId())
                .transactionDate("02/01/2022")
                .transactionValue(2.00)
                .description("Foo2")
                .documentId("DOC2")
                .category("creditcard")
                .build();
        Arrays.asList(transaction1, transaction2, transaction3).forEach(entityManager::persistAndFlush);

        Map<String, String> params = new HashMap<>();
        params.put("description", "Foo");
        params.put("category", "home");
        params.put("transactionValue", "1.00");
        params.put("transactionDate", "01/01/2022");

        //when
        List<StatementTransaction> transactions = statementTransactionRepository.findAllByStatementIdAndAllParams(1L, params);

        //then
        assertThat(transactions).isNotNull();
        assertThat(transactions).hasSize(2);
        assertThat(transactions.get(0).getDocumentId()).isEqualTo("DOC1");
        assertThat(transactions.get(1).getDocumentId()).isEqualTo("DOC1.5");
    }
}
