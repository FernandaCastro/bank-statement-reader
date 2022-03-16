package com.fcastro.statement;

import com.fcastro.statement.config.StatementCategoryConfig;
import com.fcastro.statement.config.StatementCategoryConfigRepository;
import com.fcastro.statement.config.StatementConfig;
import com.fcastro.statement.config.StatementConfigRepository;
import com.fcastro.statement.transaction.StatementTransaction;
import com.fcastro.statement.transaction.StatementTransactionRepository;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.Clock;
import java.util.*;

@Service
@AllArgsConstructor
public class StatementService {

    private static final Logger log = LoggerFactory.getLogger(StatementService.class);

    private final Environment env;
    private final StatementRepository statementRepository;
    private final StatementTransactionRepository statementTransactionRepository;
    private final StatementConfigRepository statementConfigRepository;
    private final StatementCategoryConfigRepository statementCategoryConfigRepository;


    public List<StatementTransaction> read(long clientId, long bankId, MultipartFile file) {

        StatementConfig statementConfiguration = statementConfigRepository.findByBankIdAndClientId(bankId, clientId);
        String[] transactionFields = statementConfiguration.getTransactionFields();
        if (transactionFields == null || transactionFields.length == 0) {
            log.error("Transaction fields undefined!");
        }

        Reader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(file.getInputStream(), "ISO-8859-1"));
        } catch (IOException e) {
            throw new IllegalStateException("File [" + file.getResource().getFilename() + "] could not be read.");
        }

        Map<String, String> mapping = new HashMap<String, String>();
        mapping.put(transactionFields[0], "transactionDate");
        mapping.put(transactionFields[1], "description");
        mapping.put(transactionFields[2], "documentId");
        mapping.put(transactionFields[3], "transactionValue");

        HeaderColumnNameTranslateMappingStrategy<StatementTransaction> strategy =
                new HeaderColumnNameTranslateMappingStrategy<StatementTransaction>();
        strategy.setType(StatementTransaction.class);
        strategy.setColumnMapping(mapping);

        CsvToBean<StatementTransaction> csvToBean = new CsvToBeanBuilder<StatementTransaction>(reader)
                .withMappingStrategy(strategy)
               // .withKeepCarriageReturn(true)
               // .withQuoteChar('"')
               // .withSeparator(',')
                .withIgnoreLeadingWhiteSpace(true)
                .build();

        return csvToBean.parse();
    }

    public Map<String, Double> categorize(long clientId, long bankId, List<StatementTransaction> transactions) {

        StatementConfig statementConfiguration = statementConfigRepository.findByBankIdAndClientId(bankId, clientId);
        if (statementConfiguration == null) {
            log.error("Statement Configuration is undefined.");
        }

        List<StatementCategoryConfig> statementCategories = statementCategoryConfigRepository.findAllByStatementConfigId(statementConfiguration.getId());
        if (statementCategories == null || statementCategories.isEmpty())
            return null;

        Map<String, Double> sumCategories = new HashMap<>();
        for (StatementCategoryConfig statementCategoryConfig : statementCategories) {
            List<String> tags = Arrays.asList(statementCategoryConfig.getTags().trim().toUpperCase().split("\\s*,\\s*").clone());

            sumCategories.put(statementCategoryConfig.getName(),
                    transactions.stream()
                            .filter(p -> p.getDescription() != null && !p.getDescription().isBlank())
                            .filter(p -> p.getCategory() == null || p.getCategory().isBlank())
                            .filter(p -> this.contains(p.getDescription(), tags))
                            .peek(p -> p.setCategory(statementCategoryConfig.getName()))
                            .mapToDouble(StatementTransaction::getTransactionValue).sum());
        }

        return sumCategories;
    }

    @Transactional
    public Statement save(long clientId, long bankId, String filename, List<StatementTransaction> transactions) {

        Statement statement = null;
        var statementOption = statementRepository.findByOwnerIdAndBankIdAndFilename(clientId, bankId, filename);
        if (statementOption.isEmpty()) {
            statement = new Statement();
            statement.setFilename(filename);
            statement.setClientId(clientId);
            statement.setBankId(bankId);
            statement.setProcessedAt(Clock.systemUTC().instant());
            statement = statementRepository.save(statement);
        }else{
            statement = statementOption.get();
            statement = statementRepository.updateProcessedAtById(Clock.systemUTC().instant(),statement.getId());
            statementTransactionRepository.deleteAllByStatementId(statement.getId());
        }

        for (StatementTransaction transaction:transactions) {
            transaction.setStatementId(statement.getId());
            statementTransactionRepository.save(transaction);
        }
        return statement;
    }

    @Transactional
    public void delete(long statementId){
        var statementOption = statementRepository.findById(statementId);
        if (!statementOption.isEmpty()) {
            statementTransactionRepository.deleteAllByStatementId(statementId);
            statementRepository.deleteById(statementId);
        }
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

}
