package com.example.statement;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
/*
@WebAppConfiguration
@ContextConfiguration(classes = {UploadBankStatementController.class })
@RunWith(SpringJUnit4ClassRunner.class)
*/

@WebMvcTest
public class UploadStatementControllerTest {

    //@Autowired
   // private WebApplicationContext webApplicationContext;
    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    private StatementPropertiesHandle statementPropertiesHandle;

    @SpyBean
    private StatementService statementService;

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
        mockMvc.perform(multipart("/alda/upload-csv-file").file(file))
                .andExpect(status().isOk());
    }

}
