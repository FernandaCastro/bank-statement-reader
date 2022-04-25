package com.fcastro.statementconfig;

import com.fcastro.statementconfig.category.StatementConfigCategory;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="STATEMENT_CONFIG")
@Getter @Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class StatementConfig {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private String transactionDateField;
    private String transactionValueField;
    private String descriptionField;
    private String documentIdField;

    private Long clientId;
    private Long bankId;

    @OneToMany(mappedBy="statementConfig")
    private List<StatementConfigCategory> categories;
}
