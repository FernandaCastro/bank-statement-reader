package com.example.bank;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
/*
@WebAppConfiguration
@ContextConfiguration(classes = {UploadBankStatementController.class })
@RunWith(SpringJUnit4ClassRunner.class)
*/

@RunWith(SpringRunner.class)
public class UploadBankStatementControllerTestIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testUploadCSVFile()
            throws Exception {
        String content  = "Data,Dependencia Origem,HistÛrico,Data do Balancete,N˙mero do documento,Valor,"+
                            "30/11/2020,,Saldo Anterior,,0,1814.10";

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
