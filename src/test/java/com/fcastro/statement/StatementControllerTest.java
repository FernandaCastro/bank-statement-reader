package com.fcastro.statement;

import com.fcastro.BankStatementApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@WebAppConfiguration
//@ContextConfiguration(classes = {StatementController.class })
//@RunWith(SpringJUnit4ClassRunner.class)

@RunWith(SpringJUnit4ClassRunner.class) //to benefit from Spring features in JUnit tests.
@ContextConfiguration(classes = StatementController.class) //to specify the configuration class that will be used during the test.
@WebAppConfiguration //to indicate that the Spring application context to load is a WebApplicationContext.



@WebMvcTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class StatementControllerTest {

    //@Autowired
    //private WebApplicationContext webApplicationContext;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testUploadCSVFile()
            throws Exception {
        String content  = "\"Data\",\"Dependencia Origem\",\"Histórico\",\"Data do Balancete\",\"Número do documento\",\"Valor\",\r" +
                          "\"28/10/2020\",\"\",\"Saldo Anterior\",\"\",\"0\",\"1750.31\",\r" +
                          "\"10/11/2020\",\"\",\"Pagto conta telefone - TELEMAR RJ (OI FIXO)\",\"\",\"111003\",\"-32.76\",\r"+
                          "\"12/11/2020\",\"\",\"Pagamento conta luz - LIGHT\",\"\",\"111201\",\"-14.01\",";

        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "statement.csv",
                MediaType.TEXT_PLAIN_VALUE,
                content.getBytes()
        );

        try {
            //MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
            mockMvc.perform(multipart("api/v1/statements/upload").file(file)
                    .param("ownerId", "alda")
                    .param("bankId", "bb"))
                    .andExpect(status().isOk());

        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

}
