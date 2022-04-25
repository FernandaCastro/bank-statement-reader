package com.fcastro.statementconfig.category;

import com.fcastro.statementconfig.StatementConfig;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name="STATEMENT_CONFIG_CATEGORY")
@Getter @Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class StatementConfigCategory {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String tags;

    @ManyToOne
    @JoinColumn(name="statement_config_id", nullable=false)
    private StatementConfig statementConfig;
}
