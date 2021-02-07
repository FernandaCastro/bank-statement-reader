package com.example.bank;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
/*
@WebAppConfiguration
@ContextConfiguration(classes = {UploadBankStatementController.class })
@RunWith(SpringJUnit4ClassRunner.class)
*/

@WebMvcTest
public class UploadBankStatementControllerTest {

    //@Autowired
   // private WebApplicationContext webApplicationContext;
    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    private BankStatementService bankStatementService;

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

        //MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(multipart("/upload-csv-file").file(file))
                .andExpect(status().isOk());
    }

}
