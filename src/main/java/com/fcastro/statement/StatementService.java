package com.fcastro.statement;

import com.fcastro.exception.ParseCSVException;
import com.fcastro.exception.ResourceNotFoundException;
import com.fcastro.statement.transaction.StatementTransaction;
import com.fcastro.statement.transaction.StatementTransactionRepository;
import com.fcastro.statementconfig.StatementConfig;
import com.fcastro.statementconfig.StatementConfigRepository;
import com.fcastro.statementconfig.category.StatementConfigCategory;
import com.fcastro.statementconfig.category.StatementConfigCategoryRepository;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.Clock;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingDouble;

@Service
@AllArgsConstructor
public class StatementService {

    private final StatementRepository statementRepository;
    private final StatementTransactionRepository statementTransactionRepository;
    private final StatementConfigRepository statementConfigRepository;
    private final StatementConfigCategoryRepository statementCategoryConfigRepository;

    public List<StatementTransaction> processStatementFile(long clientId, long bankId, String filename, BufferedReader reader) {

        StatementConfig statementConfig = statementConfigRepository.findByBankIdAndClientId(bankId, clientId);
        if (statementConfig==null){
            throw new ResourceNotFoundException(StatementConfig.class, clientId, bankId);
        }

        List<StatementTransaction> transactions = read(statementConfig, reader);
        transactions = categorize(statementConfig, transactions);

        return transactions;
    }

    protected List<StatementTransaction> read(StatementConfig statementConfig, BufferedReader reader) {

        Map<String, String> mapping = new HashMap<>();
        mapping.put(statementConfig.getTransactionDateField(), "transactionDate");
        mapping.put(statementConfig.getDescriptionField(), "description");
        mapping.put(statementConfig.getDocumentIdField(), "documentId");
        mapping.put(statementConfig.getTransactionValueField(), "transactionValue");

        validateHeader(reader, mapping);

        try {
            HeaderColumnNameTranslateMappingStrategy<StatementTransaction> strategy =
                    new HeaderColumnNameTranslateMappingStrategy<>();
            strategy.setType(StatementTransaction.class);
            strategy.setColumnMapping(mapping);

            CsvToBean<StatementTransaction> csvToBean = new CsvToBeanBuilder<StatementTransaction>(reader)
                    .withMappingStrategy(strategy)
                    .build();

            return csvToBean.parse();
        } catch (Exception e) {
            throw new ParseCSVException(e.getMessage());
        }
    }

    protected List<StatementTransaction> categorize(StatementConfig statementConfig, List<StatementTransaction> transactions) {

        List<StatementConfigCategory> statementCategories = statementCategoryConfigRepository.findAllByStatementConfigId(statementConfig.getId());

        if (statementCategories != null) {

            for (StatementConfigCategory statementCategoryConfig : statementCategories) {
                List<String> tags = Arrays.asList(statementCategoryConfig.getTags().trim().toUpperCase().split("\\s*,\\s*").clone());

                transactions.stream()
                        .filter(p -> p.getDescription() != null && !p.getDescription().isBlank())
                        .filter(p -> p.getCategory() == null || p.getCategory().isBlank())
                        .filter(p -> this.contains(p.getDescription(), tags))
                        .peek(p -> p.setCategory(statementCategoryConfig.getName()))
                        .collect(Collectors.toList());
            }
        }
        return transactions;
    }

    @Transactional
    public Statement save(long clientId, long bankId, String filename, List<StatementTransaction> transactions) {

        Statement statement = null;
        var statementOption = statementRepository.findByOwnerIdAndBankIdAndFilename(clientId, bankId, filename);
        if (!statementOption.isPresent()) {
            statement = Statement.builder()
                    .filename(filename)
                    .clientId(clientId)
                    .bankId(bankId)
                    .processedAt(Clock.systemUTC().instant())
                    .build();
            statement = statementRepository.save(statement);
        } else {
            statement = statementOption.get();
            statement.setProcessedAt(Clock.systemUTC().instant());
            //statementRepository.updateProcessedAtById(statement.getProcessedAt(), statement.getId());
            statement = statementRepository.save(statement);
            statementTransactionRepository.deleteAllByStatementId(statement.getId());
        }

        statement.setTransactions(new ArrayList<>());
        for (StatementTransaction transaction : transactions) {
            transaction.setStatementId(statement.getId());
            statement.getTransactions().add(statementTransactionRepository.save(transaction));
        }
        return statement;
    }

    @Transactional
    public void delete(long statementId) {
        var statementOption = statementRepository.findById(statementId);
        if (statementOption.isPresent()) {
            statementTransactionRepository.deleteAllByStatementId(statementId);
            statementRepository.deleteById(statementId);
        }
    }

    protected Map<String, Double> summarizeByCategory(Statement statement){
        Map<String, Double> sumCategories = new HashMap<>();

        if (statement.getTransactions() == null) {
            return sumCategories;
        }

        sumCategories = statement.getTransactions().stream()
                .filter(t -> t.getCategory() != null)
                .collect(groupingBy(StatementTransaction::getCategory, summingDouble(StatementTransaction::getTransactionValue)));

        return sumCategories;
    }

    public List<Statement> findAllByClientId(Long clientId) {
        return statementRepository.findAllByClientId(clientId);
    }

    public Statement findById(Long id) {
        return statementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Statement.class, id));
    }

    private boolean contains(String description, List<String> tags) {
        for (String tag : tags) {
            if (tag.startsWith("!")) {
                tag = tag.replace("!", "");
                if (description.toUpperCase().contains(tag))
                    return false;
            } else if (description.toUpperCase().contains(tag))
                return true;
        }
        return false;
    }

    private void validateHeader(BufferedReader reader, Map<String, String> columnMap) throws ParseCSVException {
        List<String> header = null;

        try {
            reader.mark(1024);
            String line = reader.readLine().replace("\"", "");
            header = Arrays.asList(line.split(","));
            reader.reset();

        }catch(IOException e){
            throw new ParseCSVException(e.getMessage());
        }

        if (columnMap == null || header == null)
            throw new ParseCSVException("Headers and Column mapping should be defined.");

        for (String key:columnMap.keySet()) {
            if (!header.contains(key)){
                throw new ParseCSVException("Header " + key + " is required. ");
            }
        }
    }

}
