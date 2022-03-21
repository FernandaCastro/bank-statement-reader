package com.fcastro.statement;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class BankStatementApplicationTests {

    @Autowired
    StatementController statementController;

    @Test
    void contextLoads() {
        //SmokeTest
        assertThat(statementController).isNotNull();
    }


    //Teste unitario
      //Sem nenhuma integracao
      //Exemplo: Teste serviço - Mock repositorio
         //Não usa anotacoes do Spring, usa Mockito
         //JUnit4 ou JUnit5

    //Teste Repositorio (unitario ou integracao pois carrega o contexto do spring)
        //Spring(SpringDataTest - contexto de banco) + TestContainers

    //Integracao
        //Teste Controller
            //Spring(@WebMVC) contexto HTTP


    //End-to-end/Blackbox

}
