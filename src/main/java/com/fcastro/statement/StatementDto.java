package com.fcastro.statement;

import lombok.*;

import java.time.Instant;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatementDto {

    private Long id;

    private Instant processedAt;

    private String filename;

    private Long clientId;

    private Long bankId;
}
