package com.fcastro.statement.config;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter @Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class StatementConfig {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;
    private long clientId;
    private long bankId;
    private String[] transactionFields;

}
