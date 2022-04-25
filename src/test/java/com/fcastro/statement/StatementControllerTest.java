package com.fcastro.statement;

import com.fcastro.exception.ResourceNotFoundException;
import com.fcastro.statement.transaction.StatementTransaction;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = StatementController.class)
public class StatementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StatementService statementService;

    @SpyBean private StatementModelAssembler assembler;

    @Test
    public void givenStatementModel_whenUpload_shouldReturnSuccess() throws Exception {

        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "statement.csv",
                MediaType.TEXT_PLAIN_VALUE,
                "content".getBytes()
        );

        List<StatementTransaction> transactions = new ArrayList<>();
        transactions.add(StatementTransaction.builder().statementId(1L).build());

        Map<String, Double> sumCategories = new HashMap<>();
        sumCategories.put("category", 1.00);

        StatementModel statementModel = StatementModel.builder().id(1L).build();
        Statement statement = Statement.builder().id(1L).build();
        statement.setTransactions(transactions);

        given(statementService.processStatementFile(anyLong(), anyLong(), anyString(), ArgumentMatchers.any(BufferedReader.class))).willReturn(transactions);
        given(statementService.save(anyLong(), anyLong(), anyString(), anyList())).willReturn(statement);
        given(statementService.summarizeByCategory(ArgumentMatchers.any(Statement.class))).willReturn(sumCategories);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/statements/upload")
                        .file(file)
                        .param("clientId", "1")
                        .param("bankId", "1"))

                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.transactions", hasSize(1)))
                .andExpect(jsonPath("$.summary", hasKey("category")))
                .andExpect(jsonPath("$._links.self.href", containsString("/api/v1/statements/1")));
    }

    @Test
    public void givenNotExistsStatement_whenGet_shouldReturnNotFound() throws Exception{
        //given
        given(statementService.findById(anyLong())).willThrow(ResourceNotFoundException.class);

        //when //then
        mockMvc.perform(get("/statements/{id}", 1)).andExpect(status().isNotFound());
    }
}
