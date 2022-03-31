package com.fcastro.statement;

import com.fcastro.statement.transaction.StatementTransactionView;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = StatementController.class)
public class StatementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StatementService statementService;

    @SpyBean private StatementViewAssembler assembler;
    @SpyBean private StatementUploadResponseModelAssembler uploadResponseAssembler;

    @Test
    public void givenStatementUploadResponse_whenUpload_shouldReturnSuccess() throws Exception {

        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "statement.csv",
                MediaType.TEXT_PLAIN_VALUE,
                "content".getBytes()
        );

        StatementView statement = StatementView.builder()
                .id(1)
                .build();

        List<StatementTransactionView> transactions = new ArrayList<>();
        transactions.add(StatementTransactionView.builder()
                .statementId(1)
                .build());

        Map<String, Double> sumCategories = new HashMap<>();
        sumCategories.put("category", 1.00);

        StatementUploadResponse uploadResponse = StatementUploadResponse.builder()
                .statement(statement)
                .transactions(transactions)
                .summary(sumCategories)
                .build();

        org.mockito.BDDMockito.given(statementService.uploadStatements(anyLong(), anyLong(), anyString(), ArgumentMatchers.any(BufferedReader.class))).willReturn(uploadResponse);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/statements/upload")
                        .file(file)
                        .param("clientId", "1")
                        .param("bankId", "1"))

                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statement.id", is(1)))
                .andExpect(jsonPath("$.transactions", hasSize(1)))
                .andExpect(jsonPath("$.summary", hasKey("category")))
                .andExpect(jsonPath("$._links.self.href", containsString("/api/v1/statements/1")))
                .andExpect(jsonPath("$._links.transactions.href", containsString("/api/v1/statements/1/transactions")));;
    }

}
