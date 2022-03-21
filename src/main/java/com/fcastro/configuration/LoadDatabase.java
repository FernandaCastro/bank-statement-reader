package com.fcastro.configuration;

import com.fcastro.bank.Bank;
import com.fcastro.bank.BankRepository;
import com.fcastro.client.Client;
import com.fcastro.client.ClientRepository;
import com.fcastro.statement.config.StatementConfig;
import com.fcastro.statement.config.StatementConfigRepository;
import com.fcastro.statement.config.category.StatementConfigCategory;
import com.fcastro.statement.config.category.StatementConfigCategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class LoadDatabase {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase(
            BankRepository bankRepository,
            ClientRepository clientRepository,
            StatementConfigRepository statementConfigRepository,
            StatementConfigCategoryRepository statementCategoryRepository) {

        Clock.systemUTC();
        return args -> {
            Bank bank = bankRepository.save(Bank.builder().name("BB").build());
            log.info("Preloading Bank" + bank.getName());

            Client client = clientRepository.save(Client.builder().name("Mary").build());
            log.info("Preloading Client" + client.getName());

            StatementConfig statementConfig = statementConfigRepository.save(
                    StatementConfig.builder()
                            .bankId(bank.getId())
                            .clientId(client.getId())
                            .transactionFields(new String[]{"Data", "Histórico", "Número do documento", "Valor"})
                            .build());
            log.info("Preloading Transaction Fields" + statementConfig.getTransactionFields());

            Map<String, String> categories = new HashMap<>();
            categories.put("income", "Benefício INSS, Crédito em conta");
            categories.put("home", "LIGHT,CEDAE,BB Seguro Auto,TELEMAR,LIGHT,VIVO RJ,SKY SERV,Tarifa Pacote de Serv,DOCinternet");
            categories.put("supermarket", "SUPERMERC");
            categories.put("personal", "!SUPERMERC, Compra com Cart");
            categories.put("creditcard", "BB ADMINISTRADORA DE CARTOES");
            categories.put("beachHouse","OK NEWS PROVEDOR,AGUAS DE JUTURNAIBA,JORGE LU,AMPLA");

            categories.forEach((name, value) -> {
                statementCategoryRepository.save(
                        StatementConfigCategory.builder()
                                .statementConfigId(statementConfig.getId())
                                .name(name)
                                .tags(value)
                                .build());
            });
            log.info("Preloading Categories");
        };
    }

}
