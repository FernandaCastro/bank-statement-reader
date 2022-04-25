package com.fcastro.statement;

import com.fcastro.exception.ParseCSVException;
import com.fcastro.statement.transaction.StatementTransaction;
import com.fcastro.statement.transaction.StatementTransactionRepository;
import com.fcastro.statementconfig.StatementConfig;
import com.fcastro.statementconfig.category.StatementConfigCategory;
import com.fcastro.statementconfig.category.StatementConfigCategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StatementServiceTest {

    @Mock private StatementConfigCategoryRepository statementCategoryRepository;
    @Mock private StatementRepository statementRepository;
    @Mock private StatementTransactionRepository statementTransactionRepository;

    @InjectMocks
    private StatementService statementService;

    private StatementConfig statementConfig;

    @BeforeEach
    private void createStatementConfig(){
        this.statementConfig = StatementConfig.builder()
                .id(1L)
                .bankId(1L)
                .clientId(1L)
                .descriptionField("Histórico")
                .documentIdField("Número do documento")
                .transactionDateField("Data")
                .transactionValueField("Valor")
                .build();
    }

    @Test
    public void givenGoodFileData_whenRead_ShouldReturnStatementTransactionList() throws Exception{

        String content = "\"Data\",\"Dependencia Origem\",\"Histórico\",\"Data do Balancete\",\"Número do documento\",\"Valor\",\r" +
                "\"28/10/2020\",\"\",\"Saldo Anterior\",\"\",\"0\",\"1750.31\",\r" +
                "\"04/12/2020\",\"\",\"TED-Crédito em Conta -  HOT ITAIPU ALI\",\"\",\"2\",\"1100.00\",\r";

        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "statement.csv",
                MediaType.TEXT_PLAIN_VALUE,
                content.getBytes(StandardCharsets.ISO_8859_1)
        );

        var reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
        List<StatementTransaction> transactions = statementService.read(statementConfig, reader);

        org.assertj.core.api.Assertions.assertThat(transactions).isNotNull();
        assertThat(transactions.size()).isEqualTo(2);
        assertThat(transactions.get(0).getTransactionDate()).isEqualTo("28/10/2020");
        assertThat(transactions.get(0).getDescription()).isEqualTo("Saldo Anterior");
        assertThat(transactions.get(0).getTransactionValue()).isLessThanOrEqualTo(1750.31);
        assertThat(transactions.get(0).getDocumentId()).isEqualTo("0");
        assertThat(transactions.get(1).getTransactionValue()).isEqualTo(1100.00);
    }

    @Test
    public void givenBadFileData_whenRead_ShouldReturnParseCSVException() throws Exception{

        String content = "\"Data\",\"Dependencia Origem\",\"MISSING_HEADER\",\"Data do Balancete\",\"Número do documento\",\"Valor\",\n" +
                         "\"28/10/2020\",\"\",\"Saldo Anterior\",\"\",\"0\",\"1750.31\",\n" +
                          "\"04/12/2020\",\"\",\"TED-Crédito   \",\"\",\"2\",\"1100.00\",";

        MockMultipartFile file
                = new MockMultipartFile(
                "statement",
                "statement.csv",
                MediaType.TEXT_PLAIN_VALUE,
                content.getBytes(StandardCharsets.ISO_8859_1)
        );

        var reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
        ParseCSVException thrown = Assertions.assertThrows(ParseCSVException.class, () -> {
            statementService.read(statementConfig, reader);
        });

        assertThat(thrown.getMessage()).contains("does not match the Statement Configuration");
    }

    @Test
    public void givenTransactionList_whenCategorizeIncome_ShouldMarkAsIncome() {
        //given
        List<StatementConfigCategory> categories = new ArrayList<>();
        categories.add(StatementConfigCategory.builder().name("income").tags("Benefício INSS, Crédito em conta").build());

        given(statementCategoryRepository.findAllByStatementConfigId(anyLong())).willReturn(categories);

        List<StatementTransaction> transactions = new ArrayList<>();
        transactions.add(StatementTransaction.builder().statementId(1L).transactionDate("28/11/2020").description("Saldo Anterior").documentId("0").transactionValue(1750.31).build());
        transactions.add(StatementTransaction.builder().statementId(1L).transactionDate("31/12/2020").description("S A L D O").documentId("0").transactionValue(44878.93).build());

        //INCOME
        transactions.add(StatementTransaction.builder().statementId(1L).transactionDate("03/12/2020").description("Benefício INSS").documentId("1").transactionValue(50000.00).build());
        transactions.add(StatementTransaction.builder().statementId(1L).transactionDate("04/12/2020").description("TED-Crédito em Conta").documentId("2").transactionValue(10000.00).build());

        //when
        transactions = statementService.categorize(statementConfig, transactions);

        //then
        assertThat(transactions.get(0).getCategory()).isBlank();
        assertThat(transactions.get(1).getCategory()).isBlank();
        assertThat(transactions.get(2).getCategory()).isEqualTo("income");
        assertThat(transactions.get(3).getCategory()).isEqualTo("income");
    }

    @Test
    public void givenTransactionList_whenCategorize_ShouldMarkAsHome() {
        //given
        List<StatementConfigCategory> categories = new ArrayList<>();
        categories.add(StatementConfigCategory.builder().name("home").tags("BB Seguro Auto,SKY SERV").build());

        given(statementCategoryRepository.findAllByStatementConfigId(anyLong())).willReturn(categories);

        List<StatementTransaction> transactions = new ArrayList<>();
        transactions.add(StatementTransaction.builder().statementId(1L).transactionDate("28/11/2020").description("Saldo Anterior").documentId("0").transactionValue(1750.31).build());
        transactions.add(StatementTransaction.builder().statementId(1L).transactionDate("31/12/2020").description("S A L D O").documentId("0").transactionValue(44878.93).build());

        //HOME
        transactions.add(StatementTransaction.builder().statementId(1L).transactionDate("07/12/2020").description("BB Seguro Auto - SEGURO AUTO BB/MAPFRE").documentId("3").transactionValue(-300.80).build());
        transactions.add(StatementTransaction.builder().statementId(1L).transactionDate("08/12/2020").description("SKY SERV").documentId("4").transactionValue(-200.00).build());

        //when
        transactions = statementService.categorize(statementConfig, transactions);

        //then
        assertThat(transactions.get(0).getCategory()).isBlank();
        assertThat(transactions.get(1).getCategory()).isBlank();
        assertThat(transactions.get(2).getCategory()).isEqualTo("home");
        assertThat(transactions.get(3).getCategory()).isEqualTo("home");
    }

    @Test
    public void givenTransactionList_whenCategorizeByNegateArgument_ShouldMarkDistinctCategories() {
        //given
        List<StatementConfigCategory> categories = new ArrayList<>();
        categories.add(StatementConfigCategory.builder().name("supermarket").tags("SUPERMERC").build());
        categories.add(StatementConfigCategory.builder().name("personal").tags("!SUPERMERC, Compra com Cart").build());


        given(statementCategoryRepository.findAllByStatementConfigId(anyLong())).willReturn(categories);

        List<StatementTransaction> transactions = new ArrayList<>();

        //SUPERMARKET
        transactions.add(StatementTransaction.builder().statementId(1L).transactionDate("28/12/2020").description("Compra com Cartão - SUPERMERCADO ESPERAN").documentId("7").transactionValue(-600.00).build());
        transactions.add(StatementTransaction.builder().statementId(1L).transactionDate("31/12/2020").description("Compra com Cartão - 31/12 12:29 SUPERMERC GUANABARA").documentId("7").transactionValue(-600.00).build());

        //PERSONAL
        transactions.add(StatementTransaction.builder().statementId(1L).transactionDate("21/12/2020").description("Compra com Cartão - 19/12 12:36 DRICA MODA INTIMA LT").documentId("6").transactionValue(-100.00).build());
        transactions.add(StatementTransaction.builder().statementId(1L).transactionDate("31/12/2020").description("Compra com Cartão - 19/12 12:36 CAROL MODA PRAIA LT").documentId("8").transactionValue(-100.00).build());

        //when
        transactions = statementService.categorize(statementConfig, transactions);

        //then
        assertThat(transactions.get(0).getCategory()).isEqualTo("supermarket");
        assertThat(transactions.get(1).getCategory()).isEqualTo("supermarket");
        assertThat(transactions.get(2).getCategory()).isEqualTo("personal");
        assertThat(transactions.get(3).getCategory()).isEqualTo("personal");
    }

    @Test
    public void givenTransactionList_whenSummarizeByCategory_ShouldSumEachCategory(){
        //given
        List<StatementTransaction> transactions = new ArrayList<>();
        //UNCATEGORIZED
        transactions.add(StatementTransaction.builder().statementId(1L).transactionDate("28/11/2020").description("Saldo Anterior").documentId("0").transactionValue(1000.00).build());
        transactions.add(StatementTransaction.builder().statementId(1L).transactionDate("31/12/2020").description("S A L D O").documentId("0").transactionValue(4000.00).build());
        //INCOME
        transactions.add(StatementTransaction.builder().statementId(1L).transactionDate("03/12/2020").description("Benefício INSS").documentId("1").transactionValue(50000.00).category("income").build());
        transactions.add(StatementTransaction.builder().statementId(1L).transactionDate("04/12/2020").description("TED-Crédito em Conta").documentId("2").transactionValue(10000.00).category("income").build());
        //HOME
        transactions.add(StatementTransaction.builder().statementId(1L).transactionDate("07/12/2020").description("BB Seguro Auto - SEGURO AUTO BB/MAPFRE").documentId("3").transactionValue(-300.00).category("home").build());
        transactions.add(StatementTransaction.builder().statementId(1L).transactionDate("08/12/2020").description("SKY SERV").documentId("4").transactionValue(-200.00).category("home").build());
        //SUPERMARKET
        transactions.add(StatementTransaction.builder().statementId(1L).transactionDate("28/12/2020").description("Compra com Cartão - SUPERMERCADO ESPERAN").documentId("7").transactionValue(-600.00).category("supermarket").build());
        transactions.add(StatementTransaction.builder().statementId(1L).transactionDate("31/12/2020").description("Compra com Cartão - 31/12 12:29 SUPERMERC GUANABARA").documentId("7").transactionValue(-600.00).category("supermarket").build());
        //PERSONAL
        transactions.add(StatementTransaction.builder().statementId(1L).transactionDate("21/12/2020").description("Compra com Cartão - 19/12 12:36 DRICA MODA INTIMA LT").documentId("6").transactionValue(-100.00).category("personal").build());
        transactions.add(StatementTransaction.builder().statementId(1L).transactionDate("31/12/2020").description("Compra com Cartão - 19/12 12:36 CAROL MODA PRAIA LT").documentId("8").transactionValue(-100.00).category("personal").build());

        Statement statement = Statement.builder().transactions(transactions).build();

        //when
        Map<String, Double> summary = statementService.summarizeByCategory(statement);

        //then
        Assertions.assertEquals(60000.00, summary.get("income"), 0);
        Assertions.assertEquals(-500.00, summary.get("home"), 0);
        Assertions.assertEquals(-1200.00, summary.get("supermarket"), 0);
        Assertions.assertEquals(-200.00, summary.get("personal"), 0);
    }

    @Test
    public void  givenExistingStatement_whenSave_shouldReturnUpdatedStatement(){

        //given
        given(statementRepository.findByOwnerIdAndBankIdAndFilename(anyLong(), anyLong(), anyString())).willReturn(Optional.of(Statement.builder().id(1L).build()));
        willDoNothing().given(statementTransactionRepository).deleteAllByStatementId(anyLong());
        given(statementRepository.save(any(Statement.class))).willReturn(Statement.builder().id(1L).build());
        given(statementTransactionRepository.save(any(StatementTransaction.class))).willReturn(StatementTransaction.builder().id(1L).build());

        List<StatementTransaction> transactions = new ArrayList<>();
        transactions.add(StatementTransaction.builder().id(1L).build());
        transactions.add(StatementTransaction.builder().id(2L).build());

        //when
        statementService.save(1, 1, "filename.csv", transactions);

        //then
        verify(statementRepository, times(1)).findByOwnerIdAndBankIdAndFilename(anyLong(), anyLong(), anyString());
        verify(statementRepository, times(1)).save(any(Statement.class));
        verify(statementTransactionRepository, times(1)).deleteAllByStatementId(anyLong());
        verify(statementTransactionRepository, times(2)).save(any(StatementTransaction.class));
    }

    @Test
    public void givenNewStatement_whenSave_shouldReturnNewStatement(){
        //given
        given(statementRepository.findByOwnerIdAndBankIdAndFilename(anyLong(), anyLong(), anyString())).willReturn(Optional.ofNullable(null));
        given(statementRepository.save(any(Statement.class))).willReturn(Statement.builder().id(1L).build());
        given(statementTransactionRepository.save(any(StatementTransaction.class))).willReturn(StatementTransaction.builder().id(1L).build());

        List<StatementTransaction> transactions = new ArrayList<>();
        transactions.add(StatementTransaction.builder().id(1L).build());
        transactions.add(StatementTransaction.builder().id(2L).build());

        //when
        statementService.save(1, 1, "filename.csv", transactions);

        //then
        verify(statementRepository, times(1)).findByOwnerIdAndBankIdAndFilename(anyLong(), anyLong(), anyString());
        verify(statementRepository, times(1)).save(any(Statement.class));
        verify(statementTransactionRepository, never()).deleteAllByStatementId(anyLong());
        verify(statementTransactionRepository, times(2)).save(any(StatementTransaction.class));
    }

    /*
    @Test
    public void givenException_whenSave_shouldRollback(){
        //given
        given(statementRepository.findByOwnerIdAndBankIdAndFilename(anyLong(), anyLong(), anyString())).willReturn(Optional.of(Statement.builder().id(1L).build()));
        given(statementRepository.save(any(Statement.class))).willReturn(Statement.builder().id(1L).build());
        willDoNothing().given(statementTransactionRepository).deleteAllByStatementId(anyLong());
        given(statementTransactionRepository.save(any(StatementTransaction.class))).willThrow(SQLException.class);

        List<StatementTransaction> transactions = new ArrayList<>();
        transactions.add(StatementTransaction.builder().id(1L).build());
        transactions.add(StatementTransaction.builder().id(2L).build());

        //when
        statementService.save(1, 1, "filename.csv", transactions);

        //then


    }*/
}
