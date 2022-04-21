package com.fcastro.bank;

import com.fcastro.JsonUtil;
import org.assertj.core.util.Strings;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//TestRestTemplate sobe o servidor Spring inteiro
//@AutoConfigureMockMVC : Spring handles the incoming HTTP request and hands it off to your controller. almost full stack is used.

@WebMvcTest(controllers = BankController.class)
public class BankControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BankRepository bankRepository;
    @SpyBean
    private BankModelAssembler assembler;
    @SpyBean
    private ModelMapper modelMapper;

    @Test
    public void givenBank_whenGetBank_shouldReturnBank() throws Exception{
        int bankId = 1;
        Bank bank = Bank.builder()
                .id(bankId)
                .name("Banco do Brasil")
                .build();

        given(bankRepository.findById(anyLong())).willReturn(Optional.of(bank));

        mockMvc.perform(get("/api/v1/banks/{id}", bankId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bankId)))
                .andExpect(jsonPath("$.name", is("Banco do Brasil")))
                .andExpect(jsonPath("$._links.self.href", containsString(Strings.concat("/api/v1/banks/", bankId))));
    }

    @Test
    public void givenBankList_whenGetBanks_shouldReturnBankList() throws Exception{
        List<Bank> banks = new ArrayList<>();
        banks.add(Bank.builder().id(1).name("Banco do Brasil").build());
        banks.add(Bank.builder().id(2).name("Itaú").build());

        given(bankRepository.findAll()).willReturn(banks);

        mockMvc.perform(get("/api/v1/banks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.bankModelList", hasSize(2)))
                .andExpect(jsonPath("$._embedded.bankModelList[0].name", is("Banco do Brasil")))
                .andExpect(jsonPath("$._embedded.bankModelList[1].name", is("Itaú")))
                .andExpect(jsonPath("$._links.self.href", containsString("/api/v1/banks")));
    }

    @Test
    public void givenNull_whenGetBankId_shouldReturnNotFound() throws Exception{

        given(bankRepository.findById(anyLong()))
                .willReturn(Optional.ofNullable(null));

        mockMvc.perform(get("/api/v1/banks/{id}", 1))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type", is("urn:general:resource-not-found")));
    }

    @Test
    public void givenBank_whenPostNewBank_shouldReturnCreateBank() throws Exception{
        int bankId = 1;
        Bank bank = Bank.builder()
                        .name("HSBC")
                        .build();

        BankModel bankModel = modelMapper.map(bank, BankModel.class);
        bank.setId(bankId);

        given(bankRepository.save(any())).willReturn(bank);

        mockMvc.perform(post("/api/v1/banks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(bankModel)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(bankId)))
                .andExpect(jsonPath("$.name", is("HSBC")))
                .andExpect(jsonPath("$._links.self.href", containsString(Strings.concat("/api/v1/banks/", bankId))));
    }

    @Test
    public void givenBankAndBankId_whenPostExistingBank_shouldUpdateBank() throws Exception{
        int bankId = 1;
        Bank bank = Bank.builder()
                .id(bankId)
                .name("HSBC")
                .build();

        BankModel bankModel = modelMapper.map(bank, BankModel.class);

        given(bankRepository.findById(anyLong())).willReturn(Optional.of(bank));
        given(bankRepository.save(any())).willReturn(bank);

        mockMvc.perform(put("/api/v1/banks/{id}", bankId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(bankModel)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(bankId)))
                .andExpect(jsonPath("$.name", is("HSBC")))
                .andExpect(jsonPath("$._links.self.href", containsString(Strings.concat("/api/v1/banks/", bankId))));
    }

    @Test
    public void givenBankAndBankId_whenPostNotExistingBank_shouldCreateBank() throws Exception{
        int bankId = 1;
        Bank bank = Bank.builder()
                .id(bankId)
                .name("HSBC")
                .build();

        BankModel bankModel = modelMapper.map(bank, BankModel.class);

        given(bankRepository.findById(anyLong())).willReturn(Optional.ofNullable(null));
        given(bankRepository.save(any())).willReturn(bank);

        mockMvc.perform(put("/api/v1/banks/{id}", bankId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(bankModel)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(bankId)))
                .andExpect(jsonPath("$.name", is("HSBC")))
                .andExpect(jsonPath("$._links.self.href", containsString(Strings.concat("/api/v1/banks/", bankId))));
    }

    @Test
    public void givenAnyBankId_whenDelete_shouldReturnNoContent() throws Exception{
        int bankId = 1;
        doNothing().when(bankRepository).deleteById(anyLong());

        mockMvc.perform(delete("/api/v1/banks/{id}", bankId))
                .andExpect(status().isNoContent());
    }

}
