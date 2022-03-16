package com.fcastro.statement;

import com.fcastro.BankStatementApplication;
import com.fcastro.statement.transaction.StatementTransaction;
import com.fcastro.statement.transaction.StatementTransactionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = BankStatementApplication.class)
public class StatementServiceTest {

    @Autowired
    private StatementService statementService;

    @Autowired
    private StatementRepository statementRepository;

    @Autowired
    private StatementTransactionRepository statementTransactionRepository;


    @Test
    public void testRead() {
        String content = "\"Data\",\"Dependencia Origem\",\"Histórico\",\"Data do Balancete\",\"Número do documento\",\"Valor\",\n" +
                "\"28/10/2020\",\"\",\"Saldo Anterior\",\"\",\"0\",\"1750.31\",";

        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "statement.csv",
                MediaType.TEXT_PLAIN_VALUE,
                content.getBytes(StandardCharsets.ISO_8859_1)
        );

        try {
            List<StatementTransaction> transactions = statementService.read(1, 1, file);

            org.assertj.core.api.Assertions.assertThat(transactions).isNotNull();
            assertThat(transactions.size()).isEqualTo(1);
            assertThat(transactions.get(0).getTransactionDate()).isEqualTo("28/10/2020");
            assertThat(transactions.get(0).getDescription()).isEqualTo("Saldo Anterior");
            assertThat(transactions.get(0).getTransactionValue()).isLessThanOrEqualTo(1750.31);
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testCategorize() {
        try {
            String content = "\"Data\",\"Dependencia Origem\",\"Histórico\",\"Data do Balancete\",\"Número do documento\",\"Valor\",\n" +
                    "\"28/11/2020\",\"\",\"Saldo Anterior\",\"\",\"0\",\"1750.31\"," +
                    "\"03/12/2020\",\"\",\"Benefício INSS\",\"\",\"1\",\"50000.00\"," +
                    "\"04/12/2020\",\"\",\"TED-Crédito em Conta -  HOT ITAIPU ALI\",\"\",\"2\",\"1100.00\"," +
                    "\"07/12/2020\",\"\",\"BB Seguro Auto - SEGURO AUTO BB/MAPFRE\",\"\",\"3\",\"-300.80\"," +
                    "\"10/12/2020\",\"\",\"Pagamento de Boleto - BB ADMINISTRADORA DE CARTOES\",\"\",\"4\",\"-402.35\"," +
                    "\"15/12/2020\",\"\",\"Pgto conta água - CONC. AGUAS DE JUTURNAIBA\",\"\",\"5\",\"-80.46\"," +
                    "\"21/12/2020\",\"\",\"Compra com Cartão - 19/12 12:36 DRICA MODA INTIMA LT\",\"\",\"6\",\"-101.00\"," +
                    "\"31/12/2020\",\"\",\"Compra com Cartão - 31/12 12:29 SUPERMERCADO ESPERAN\",\"\",\"7\",\"-101.02\"," +
                    "\"31/12/2020\",\"\",\"Compra com Cartão - 19/12 12:36 CAROL MODA PRAIA LT\",\"\",\"8\",\"-101.00\"," +
                    "\"31/12/2020\",\"\",\"S A L D O\",\"\",\"0\",\"44878.93\"";

            MockMultipartFile file
                    = new MockMultipartFile(
                    "file",
                    "statement.csv",
                    MediaType.TEXT_PLAIN_VALUE,
                    content.getBytes(StandardCharsets.ISO_8859_1));

            List<StatementTransaction> transactions = statementService.read(1, 1, file);
            Map<String, Double> soma = statementService.categorize(1, 1, transactions);

            Assertions.assertEquals(51100.00, soma.get("INCOME"), 0);
            Assertions.assertEquals(-300.80, soma.get("HOME"), 0);
            Assertions.assertEquals(-80.46, soma.get("BEACHHOUSE"), 0);
            Assertions.assertEquals(-101.02, soma.get("SUPERMARKET"), 0);
            Assertions.assertEquals(-202.00, soma.get("PERSONAL"), 0);
            Assertions.assertEquals(-402.35, soma.get("CREDITCARD"), 0);
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testSave() {
        String content = "\"Data\",\"Dependencia Origem\",\"Histórico\",\"Data do Balancete\",\"Número do documento\",\"Valor\",\n" +
                "\"28/10/2020\",\"\",\"Saldo Anterior\",\"\",\"0\",\"1750.31\",";

        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "testSave.csv",
                MediaType.TEXT_PLAIN_VALUE,
                content.getBytes(StandardCharsets.ISO_8859_1));

        try {
            List<StatementTransaction> transactions = statementService.read(1, 1, file);
            statementService.save(1, 1, file.getName(), transactions);

            var statement = statementRepository.findByOwnerIdAndBankIdAndFilename(1, 1, file.getName());
            Assertions.assertTrue(statement.isPresent());

            var transacions = statementTransactionRepository.findAllByStatementId(statement.get().getId());
            Assertions.assertNotNull(transacions);
            Assertions.assertEquals(1, transacions.size());

        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testSaveSameFilenameTwice() {
        String content = "\"Data\",\"Dependencia Origem\",\"Histórico\",\"Data do Balancete\",\"Número do documento\",\"Valor\",\n" +
                "\"28/10/2020\",\"\",\"Saldo Anterior\",\"\",\"0\",\"1750.31\"," +
                "\"29/10/2020\",\"\",\"TED-Crédito em Conta -  HOT ITAIPU ALI\",\"\",\"1\",\"1100.00\",";

        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "testSaveSameFilenameTwice.csv",
                MediaType.TEXT_PLAIN_VALUE,
                content.getBytes(StandardCharsets.ISO_8859_1));

        try {
            List<StatementTransaction> transactions = statementService.read(1, 1, file);
            statementService.save(1, 1, file.getName(), transactions);
            statementService.save(1, 1, file.getName(), transactions);

        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

}
