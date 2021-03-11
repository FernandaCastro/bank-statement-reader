package com.example.statement;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class StatementService {

    @Autowired
    private Environment env;

    @Autowired
    private StatementPropertiesHandle statementPropertiesHandle;

    @Autowired(required = false)
    private OwnerRepository ownerRepository;

    @Autowired(required = false)
    private StatementRepository statementRepository;

    @Autowired(required = false)
    private StatementTransactionRepository statementTransactionRepository;

    public List<StatementTransaction> read(Bank bank, MultipartFile file) throws Exception {

        String[] transactionFields = env.getProperty("statement." + bank.getId().toLowerCase() + ".transactionFields", String[].class);
        if (transactionFields == null || transactionFields.length == 0)
            throw new Exception("Unable to find transaction fields for bank: " + bank.getId());

        // parse CSV file to create a list of `User` objects
        Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), "ISO-8859-1"));

        Map<String, String> mapping = new HashMap<String, String>();
        mapping.put(transactionFields[0], "transactionDate");
        mapping.put(transactionFields[1], "description");
        mapping.put(transactionFields[2], "documentId");
        mapping.put(transactionFields[3], "transactionValue");

        HeaderColumnNameTranslateMappingStrategy<StatementTransaction> strategy =
                new HeaderColumnNameTranslateMappingStrategy<StatementTransaction>();
        strategy.setType(StatementTransaction.class);
        strategy.setColumnMapping(mapping);

        // create csv bean builder
        CsvToBean<StatementTransaction> csvToBean = new CsvToBeanBuilder<StatementTransaction>(reader)
                .withMappingStrategy(strategy)
               // .withKeepCarriageReturn(true)
               // .withQuoteChar('"')
               // .withSeparator(',')
                .withIgnoreLeadingWhiteSpace(true)
                .build();

        // convert `CsvToBean` object to list of transactions
        return csvToBean.parse();
    }

    public Map<String, Double> categorize(Owner owner, Bank bank, List<StatementTransaction> transactions) throws Exception {

        var baseCategories = statementPropertiesHandle.getCategories(bank.getId());
        var customCategories = statementPropertiesHandle.getCategories(owner.getId());
        Map<String, String> categories = Stream.of(baseCategories, customCategories)
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (v1, v2) -> v1.equalsIgnoreCase(v2)
                                ? v1
                                : v1 + ", " + v2));


        if (categories.isEmpty())
            throw new Exception("Unable to find any category");

        Map<String, Double> sumCategories = new HashMap<>();
        for (String category : categories.keySet()) {
            List<String> tags = Arrays.asList(categories.get(category).trim().toUpperCase().split("\\s*,\\s*").clone());

            sumCategories.put(category,
                    transactions.stream()
                            .filter(p -> p.getDescription() != null && !p.getDescription().isBlank())
                            .filter(p -> p.getCategory() == null || p.getCategory().isBlank())
                            .filter(p -> this.contains(p.getDescription(), tags))
                            .peek(p -> p.setCategory(category))
                            .mapToDouble(StatementTransaction::getTransactionValue).sum());
        }

        return sumCategories;
    }

    public void save(Owner owner, Bank bank, String filename, List<StatementTransaction> transactions) throws Exception{
        var statement = new Statement();
        statement.setStartDate(transactions.get(0).getTransactionDate());
        statement.setEndDate(transactions.get(transactions.size()-1).getTransactionDate());
        statement.setFilename(filename);
        statement.setOwnerId(owner.getId());
        statement.setBankId(bank.getId());
        statement = statementRepository.save(statement);

        //TODO: define rollback of transactions and statement : save all or nothing
        for (StatementTransaction transaction:transactions) {
            transaction.setStatementId(statement.getId());
            statementTransactionRepository.save(transaction);
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
