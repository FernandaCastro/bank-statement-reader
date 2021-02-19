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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatementService {

    @Autowired
    private Environment env;

    @Autowired
    private StatementPropertiesHandle statementPropertiesHandle;

    public List<Transaction> read(MultipartFile file, String owner) throws Exception {

        String[] transactionFields = env.getProperty("statement." + owner + ".transactionFields", String[].class);
        if (transactionFields == null || transactionFields.length == 0)
            throw new Exception("Unable to find transaction fields for owner: " + owner);

        // parse CSV file to create a list of `User` objects
        Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), "ISO-8859-1"));

        Map<String, String> mapping = new HashMap<String, String>();
        mapping.put(transactionFields[0], "date");
        mapping.put(transactionFields[1], "description");
        mapping.put(transactionFields[2], "id");
        mapping.put(transactionFields[3], "value");

        HeaderColumnNameTranslateMappingStrategy<Transaction> strategy =
                new HeaderColumnNameTranslateMappingStrategy<Transaction>();
        strategy.setType(Transaction.class);
        strategy.setColumnMapping(mapping);

        // create csv bean builder
        CsvToBean<Transaction> csvToBean = new CsvToBeanBuilder<Transaction>(reader)
                .withMappingStrategy(strategy)
                .withIgnoreLeadingWhiteSpace(true)
                .build();

        // convert `CsvToBean` object to list of transactions
        return csvToBean.parse();
    }

    public Map<String, Double> categorize(String owner, List<Transaction> aldaTransactions) throws Exception {

        Map<String, String> categories = statementPropertiesHandle.getCategories(owner);
        if (categories == null || categories.isEmpty())
            throw new Exception("Unable to find categories for owner: " + owner);

        Map<String, Double> sumCategories = new HashMap<>();
        for (String category : categories.keySet()) {
            List<String> tags = Arrays.asList(categories.get(category).trim().toUpperCase().split("\\s*,\\s*").clone());

            sumCategories.put(category,
                    aldaTransactions.stream()
                            .filter(p -> p.getDescription() != null && !p.getDescription().isBlank())
                            .filter(p -> p.getCategory() == null || p.getCategory().isBlank())
                            .filter(p -> this.contains(p.getDescription(), tags))
                            .peek(p -> p.setCategory(category))
                            .mapToDouble(Transaction::getValue).sum());
        }

        return sumCategories;
    }

    public boolean contains(String description, List<String> tags) {
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
