package com.fcastro.statement;

import lombok.*;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name="STATEMENT")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Statement {

    @Id
    @GeneratedValue (strategy= GenerationType.IDENTITY)
    private Long id;

    //@Temporal(TemporalType.TIMESTAMP)
    private Instant processedAt;

    private String filename;

    private Long clientId;

    private Long bankId;
}
