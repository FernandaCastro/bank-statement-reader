package com.fcastro;

import com.fcastro.statement.StatementController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class BankStatementApplicationTest {

    @Autowired
    StatementController statementController;

    @Test
    void contextLoads() {
        //SmokeTest or Sanity Check
        //It will fail if the application context cannot start
        assertThat(statementController).isNotNull();
    }

    //1 - Smoke Test/Sanity Check - Start the entire application
    //    @SpringBootTest
    //    This annotation tells Spring Boot to look for a main configuration class (one with @SpringBootApplication, for instance) and use that to
    //    start a Spring application context.
    //    The app context is cached between tests, meaning the app is started only once. In order to control and clean the cache use @DirtiesContext.

    //2 - Start entire application and listen for connections, then send an HTTP request and assert the response
    //    @SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
    //    @LocalServerPort int port
    //    @Autowired TestRestTemplate

    //3 - Not start the server but only the layer where Spring handles the incoming HTTP request and hands it off to your controller.
    //    That way, almost of the full stack is used, and your code will be called in exactly the same way as if it were processing a real HTTP request
    //    but without the cost of starting the server.
    //    @SpringBootTest
    //    @AutoConfigureMockMvc injects the Spring's MockMvc
    //    @Autowired MockMvc

    //4 - The full Spring application context is started but without the server
    //    @WebMvcTest(HomeController.class)  - It narrows the tests to only the web layer and even make it strict to only one controller.
    //    @Autowired MockMvc

    //    The test (3 and 4) are the same. However, in the 4, Spring Boot instantiates only the web layer rather than the whole context.

    //    @WebMvcTest also offers @MockBean, to create and inject a mock for the service
    //    (if you do not do so, the application context cannot start) and we set its expectations using Mockito.

    // REPOSITORY LAYER
    //5 - @DataJpaTest : if you want to test JPA applications. By default, it will configure an in-memory embedded database,
    //                   scans for @Entity classes and configure Spring Data JPA repositories. Regular @Component beans will
    //                   not be loaded into the ApplicationContext.
    //                   Data JPA tests are transactional and rollback at the end of each test by default,
    //    @RunWith(SpringRunner.class)
    //    @DataJpaTest  injects a TestEntityManager, an alternative to the standard JPA, also offers JdbcTemplate.
    //    @Transactional(propagation = Propagation.NOT_SUPPORTED) To control the transactional level
    //
    //    The TestEntityManager can be used outside @DataJpaTests, using @AutoConfigureTestEntityManager.
    //    By default it runs agains an in-memory database, but a real database can be used instead by using @AutoConfigureTestDatabase(replace=Replace.NONE)

    //6 - @JdbcTest is similar to @DataJpaTest but is for tests that only require a DataSource and do not use Spring Data JDBC.
    //    By default, it configures an in-memory embedded database and a JdbcTemplate



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
