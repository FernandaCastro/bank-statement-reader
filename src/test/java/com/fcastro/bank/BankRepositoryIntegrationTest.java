package com.fcastro.bank;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class BankRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BankRepository repository;

    @Test
    void givenBank_shouldReturnBankFromDatabase() throws Exception {
        //given
        Bank bank = this.entityManager.persistFlushFind(Bank.builder().name("Itaú").build());

        //when
        List<Bank> banks = this.repository.findAll();

        //then
        assertThat(banks).contains(bank);
    }


}
