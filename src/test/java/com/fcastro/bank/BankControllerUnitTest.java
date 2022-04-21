package com.fcastro.bank;

import org.assertj.core.util.Strings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class BankControllerUnitTest {

    @Mock private BankRepository bankRepository;
    @Spy private BankModelAssembler assembler;
    @Spy private ModelMapper modelMapper;

    @InjectMocks
    private BankController bankController;

    @Test
    public void givenBankId_whenBankExists_shouldReturnBank() throws Exception{
        long bankId = 1L;
        Bank bank = Bank.builder()
                .id(bankId)
                .name("BB")
                .build();

        given(bankRepository.findById(bankId))
                .willReturn(Optional.of(bank));

        ResponseEntity<BankModel> result = bankController.one(bankId);

        assertThat(result, notNullValue());
        assertThat(result.getBody(), notNullValue());
        assertThat(result.getBody().getId(), equalTo(bankId));
        assertThat(result.getBody().getName(), equalTo("BB"));
        assertThat(result.getBody().getLinks(), notNullValue());
        assertThat(result.getBody().getRequiredLink(IanaLinkRelations.SELF).toUri(), hasToString(Strings.concat("/api/v1/banks/", bankId)));
    }
}
