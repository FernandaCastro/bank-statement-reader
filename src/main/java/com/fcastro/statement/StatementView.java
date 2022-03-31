package com.fcastro.statement;

import lombok.*;

import java.time.Instant;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatementView {

    private long id;

    private Instant processedAt;

    private String filename;

    private long clientId;

    private long bankId;
}
