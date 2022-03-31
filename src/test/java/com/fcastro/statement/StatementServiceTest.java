package com.fcastro.statement;

import com.fcastro.exception.ParseCSVException;
import com.fcastro.statement.config.StatementConfig;
import com.fcastro.statement.config.category.StatementConfigCategory;
import com.fcastro.statement.config.category.StatementConfigCategoryRepository;
import com.fcastro.statement.transaction.StatementTransaction;
import org.junit.jupiter.api.Assertions;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class StatementServiceTest {

    @Mock private StatementConfigCategoryRepository statementCategoryRepository;

    @InjectMocks
    private StatementService statementService;

    @Test
    public void givenGoodFileData_whenRead_ShouldReturnStatementTransactionList() throws Exception{

        StatementConfig statementConfig = StatementConfig.builder()
                .id(1)
                .bankId(1)
                .clientId(1)
                .descriptionField("Histórico")
                .documentIdField("Número do documento")
                .transactionDateField("Data")
                .transactionValueField("Valor")
                .build();

        String content = "\"Data\",\"Dependencia Origem\",\"Histórico\",\"Data do Balancete\",\"Número do documento\",\"Valor\",\n" +
                "\"28/10/2020\",\"\",\"Saldo Anterior\",\"\",\"0\",\"1750.31\",\n" +
                "\"04/12/2020\",\"\",\"TED-Crédito em Conta -  HOT ITAIPU ALI\",\"\",\"2\",\"1100\",\n";

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
        StatementConfig statementConfig = StatementConfig.builder()
                .id(1)
                .bankId(1)
                .clientId(1)
                .descriptionField("Histórico")
                .documentIdField("Número do documento")
                .transactionDateField("Data")
                .transactionValueField("Valor")
                .build();

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
    public void givenTransactionList_whenCategorizeIncome_ShouldReturnIncomeSummary() {

        StatementConfig statementConfig = StatementConfig.builder()
                .id(1)
                .bankId(1)
                .clientId(1)
                .build();

        List<StatementConfigCategory> categories = new ArrayList<>();
        categories.add(StatementConfigCategory.builder().name("income").tags("Benefício INSS, Crédito em conta").build());

        given(statementCategoryRepository.findAllByStatementConfigId(anyLong())).willReturn(categories);

        List<StatementTransaction> transactions = new ArrayList<>();
        transactions.add(StatementTransaction.builder().statementId(1).transactionDate("28/11/2020").description("Saldo Anterior").documentId("0").transactionValue(1750.31).build());
        transactions.add(StatementTransaction.builder().statementId(1).transactionDate("31/12/2020").description("S A L D O").documentId("0").transactionValue(44878.93).build());

        //INCOME
        transactions.add(StatementTransaction.builder().statementId(1).transactionDate("03/12/2020").description("Benefício INSS").documentId("1").transactionValue(50000.00).build());
        transactions.add(StatementTransaction.builder().statementId(1).transactionDate("04/12/2020").description("TED-Crédito em Conta").documentId("2").transactionValue(10000.00).build());


        Map<String, Double> summary = statementService.categorize(statementConfig, transactions);

        Assertions.assertEquals(60000.00, summary.get("income"), 0);
    }

    @Test
    public void givenTransactionList_whenCategorize_ShouldReturnCategorySummary() {

        StatementConfig statementConfig = StatementConfig.builder()
                .id(1)
                .bankId(1)
                .clientId(1)
                .build();

        List<StatementConfigCategory> categories = new ArrayList<>();
        categories.add(StatementConfigCategory.builder().name("home").tags("BB Seguro Auto,SKY SERV").build());

        given(statementCategoryRepository.findAllByStatementConfigId(anyLong())).willReturn(categories);

        List<StatementTransaction> transactions = new ArrayList<>();
        transactions.add(StatementTransaction.builder().statementId(1).transactionDate("28/11/2020").description("Saldo Anterior").documentId("0").transactionValue(1750.31).build());
        transactions.add(StatementTransaction.builder().statementId(1).transactionDate("31/12/2020").description("S A L D O").documentId("0").transactionValue(44878.93).build());

        //HOME
        transactions.add(StatementTransaction.builder().statementId(1).transactionDate("07/12/2020").description("BB Seguro Auto - SEGURO AUTO BB/MAPFRE").documentId("3").transactionValue(-300.80).build());
        transactions.add(StatementTransaction.builder().statementId(1).transactionDate("08/12/2020").description("SKY SERV").documentId("4").transactionValue(-200.00).build());

        Map<String, Double> summary = statementService.categorize(statementConfig, transactions);

        Assertions.assertEquals(-500.80, summary.get("home"), 0);
    }

    @Test
    public void givenTransactionList_whenCategorizeByNegateArgument_ShouldReturnDistinctCategoriesSummary() {

        StatementConfig statementConfig = StatementConfig.builder()
                .id(1)
                .bankId(1)
                .clientId(1)
                .build();

        List<StatementConfigCategory> categories = new ArrayList<>();
        categories.add(StatementConfigCategory.builder().name("supermarket").tags("SUPERMERC").build());
        categories.add(StatementConfigCategory.builder().name("personal").tags("!SUPERMERC, Compra com Cart").build());


        given(statementCategoryRepository.findAllByStatementConfigId(anyLong())).willReturn(categories);

        List<StatementTransaction> transactions = new ArrayList<>();

        //SUPERMARKET
        transactions.add(StatementTransaction.builder().statementId(1).transactionDate("28/12/2020").description("Compra com Cartão - SUPERMERCADO ESPERAN").documentId("7").transactionValue(-600.00).build());
        transactions.add(StatementTransaction.builder().statementId(1).transactionDate("31/12/2020").description("Compra com Cartão - 31/12 12:29 SUPERMERC GUANABARA").documentId("7").transactionValue(-600.00).build());

        //PERSONAL
        transactions.add(StatementTransaction.builder().statementId(1).transactionDate("21/12/2020").description("Compra com Cartão - 19/12 12:36 DRICA MODA INTIMA LT").documentId("6").transactionValue(-100.00).build());
        transactions.add(StatementTransaction.builder().statementId(1).transactionDate("31/12/2020").description("Compra com Cartão - 19/12 12:36 CAROL MODA PRAIA LT").documentId("8").transactionValue(-100.00).build());

        Map<String, Double> summary = statementService.categorize(statementConfig, transactions);

        Assertions.assertEquals(-200.00, summary.get("personal"), 0);
        Assertions.assertEquals(-1200.00, summary.get("supermarket"), 0);
    }
}
