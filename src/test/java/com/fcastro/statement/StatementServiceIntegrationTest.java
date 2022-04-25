package com.fcastro.statement;

import com.fcastro.statement.transaction.StatementTransaction;
import com.fcastro.statement.transaction.StatementTransactionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class StatementServiceIntegrationTest {

    @Autowired
    private StatementRepository statementRepository;

    @Autowired
    private StatementTransactionRepository statementTransactionRepository;

    @Autowired
    private StatementService statementService;

    @Test
    public void givenNewStatementAndTransactions_whenSave_thenSaveStatementAndTransactions(){
        //given
        List<StatementTransaction> transactions = new ArrayList<>();
        transactions.add(StatementTransaction.builder()
                .transactionValue(1.00)
                .transactionDate("31/03/2022")
                .description("Transaction1")
                .documentId("T1")
                .build());

        transactions.add(StatementTransaction.builder()
                .transactionValue(2.00)
                .transactionDate("01/04/2022")
                .description("Transaction2")
                .documentId("T2")
                .build());
        //when
        Statement statement = statementService.save(1, 1, "TestNewStatement.csv", transactions);

        //then
        assertThat(statement, is(notNullValue()));
        assertThat(statement.getId(), greaterThan(Long.valueOf(0)));
        assertThat(statement.getFilename(), equalTo("TestNewStatement.csv"));

        List<StatementTransaction> storedTransactions = statementTransactionRepository.findAllByStatementId(statement.getId());
        assertThat(storedTransactions, is(notNullValue()));
        assertThat(storedTransactions.size(), equalTo(2));
        assertThat(storedTransactions.get(0).getTransactionValue(), equalTo(1.00));
        assertThat(storedTransactions.get(1).getTransactionValue(), equalTo(2.00));
    }

    @Test
    public void givenExistingStatement_whenSave_thenUpdateStatementAndRecreateTransactions(){
        //given
        List<StatementTransaction> inputTransactions = new ArrayList<>();
        inputTransactions.add(StatementTransaction.builder().transactionValue(1.00).transactionDate("31/03/2022").description("Transaction1").documentId("T1").build());
        inputTransactions.add(StatementTransaction.builder().transactionValue(2.00).transactionDate("31/03/2022").description("Transaction2").documentId("T2").build());
        Statement statement = statementService.save(1, 1, "TestNewStatement.csv", inputTransactions);
        Instant processedAt = statement.getProcessedAt();

        //when
        Statement updatedStatement = statementService.save(1, 1, "TestNewStatement.csv", inputTransactions);

        //then
        assertThat(updatedStatement.getProcessedAt(), not(processedAt));

        List<StatementTransaction> updatedTransactions = statementTransactionRepository.findAllByStatementId(statement.getId());
        assertThat(updatedTransactions.size(), equalTo(2));
        assertThat(updatedTransactions.get(0).getId(), not(inputTransactions.get(0).getId()));
        assertThat(updatedTransactions.get(1).getId(), not(inputTransactions.get(1).getTransactionValue()));
    }
}
