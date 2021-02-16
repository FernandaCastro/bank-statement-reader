package com.example.bank;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;

@Service
public class BankStatementService {

    @Value("${transaction.category.rio}")
    private String rio;
    @Value("${transaction.category.saquarema}")
    private String saquarema;
    @Value("${transaction.category.supermarket}")
    private String supermarket;
    @Value("${transaction.category.personal}")
    private String personal;
    @Value("${transaction.category.creditcard}")
    private String creditCard;

    public List<AldaTransaction> read(MultipartFile file, String owner) throws Exception {

        Class transaction = Transaction.class;
        if (owner.equalsIgnoreCase("alda"))
            transaction = AldaTransaction.class;

        // parse CSV file to create a list of `User` objects
        Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), "ISO-8859-1"));

        // create csv bean reader
        CsvToBean<AldaTransaction> csvToBean = new CsvToBeanBuilder<AldaTransaction>(reader)
                .withType(transaction)
                .withIgnoreLeadingWhiteSpace(true)
                .build();

        // convert `CsvToBean` object to list of transactions
        return csvToBean.parse();
    }

    public double categorizeRio(List<AldaTransaction> aldaTransactions){
        List<String> rioProperties = Arrays.asList(rio.trim().split(",").clone());

        return aldaTransactions.stream()
                .filter(p -> p.getCategory() == null || p.getCategory().isBlank())
                .filter(p -> this.contains(p.getDescription(), rioProperties))
                .peek(p -> p.setCategory("RIO"))
                .mapToDouble(AldaTransaction::getValue).sum();
    }

    public double categorizeSaquarema(List<AldaTransaction> aldaTransactions){
        List<String> saquaremaProperties = Arrays.asList(saquarema.trim().split(",").clone());

        return aldaTransactions.stream()
                .filter(p -> p.getCategory() == null || p.getCategory().isBlank())
                .filter(p -> this.contains(p.getDescription(), saquaremaProperties))
                .peek(p -> p.setCategory("SAQUAREMA"))
                .mapToDouble(AldaTransaction::getValue).sum();
    }

    public double categorizeSupermarket(List<AldaTransaction> aldaTransactions){
        List<String> supermarketProperties = Arrays.asList(supermarket.trim().split(",").clone());

        return aldaTransactions.stream()
                .filter(p -> p.getCategory() == null || p.getCategory().isBlank())
                .filter(p -> this.contains(p.getDescription(), supermarketProperties))
                .peek(p -> p.setCategory("MERCADO"))
                .mapToDouble(AldaTransaction::getValue).sum();
    }

    public double categorizePersonal(List<AldaTransaction> aldaTransactions){
        List<String> personalProperties = Arrays.asList(personal.trim().split(",").clone());
        List<String> supermarketProperties = Arrays.asList(supermarket.trim().split(",").clone());

        return aldaTransactions.stream()
                .filter(p -> p.getCategory() == null || p.getCategory().isBlank())
                .filter(p -> this.contains(p.getDescription(), personalProperties))
                .filter(p -> !this.contains(p.getDescription(), supermarketProperties))
                .peek(p -> p.setCategory("PESSOAL"))
                .mapToDouble(AldaTransaction::getValue).sum();
    }

    public double categorizeCreditCard(List<AldaTransaction> aldaTransactions){
        List<String> creditCardProperties = Arrays.asList(creditCard.trim().split(",").clone());

        return aldaTransactions.stream()
                .filter(p -> p.getCategory() == null || p.getCategory().isBlank())
                .filter(p -> this.contains(p.getDescription(), creditCardProperties))
                .peek(p -> p.setCategory("CARTAO"))
                .mapToDouble(AldaTransaction::getValue).sum();
    }

    public boolean contains(String description, List<String> categoryProperties){
        for(String prop : categoryProperties) {
            if (description.toUpperCase().contains(prop.toUpperCase())) {
                return true;
            }
        }
        return false;
    }
}
