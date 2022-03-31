package com.fcastro.statement.config;

import lombok.*;

@Getter @Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class StatementConfigView {

    private long id;
    private long clientId;
    private long bankId;
    private String[] transactionFields;
}
