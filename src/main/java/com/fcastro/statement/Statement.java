package com.fcastro.statement;

import com.fcastro.statement.transaction.StatementTransaction;
import lombok.*;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

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

    @OneToMany(mappedBy="statement")
    private List<StatementTransaction> transactions;
}
