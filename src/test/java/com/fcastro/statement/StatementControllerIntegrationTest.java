package com.fcastro.statement;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class StatementControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void givenStatementModel_whenUpload_shouldReturnSuccess() throws Exception {
        //given
        String content  = "\"Data\",\"Dependencia Origem\",\"Histórico\",\"Data do Balancete\",\"Número do documento\",\"Valor\",\r" +
                "\"28/10/2020\",\"\",\"Saldo Anterior\",\"\",\"0\",\"1750.31\",\r" +
                "\"10/11/2020\",\"\",\"Pagto conta telefone - TELEMAR RJ (OI FIXO)\",\"\",\"111003\",\"-32.76\",\r"+
                "\"12/11/2020\",\"\",\"Pagamento conta luz - LIGHT\",\"\",\"111201\",\"-14.01\",\r";

        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "statement.csv",
                MediaType.TEXT_PLAIN_VALUE,
                content.getBytes(StandardCharsets.ISO_8859_1)
        );

        //when //then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/statements/upload")
                        .file(file)
                        .param("clientId", "1")
                        .param("bankId", "1"))

                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.transactions", hasSize(3)))
                .andExpect(jsonPath("$.summary", hasKey("home")))
                .andExpect(jsonPath("$._links.self.href", containsString("/api/v1/statements/1")));
    }
}
