package com.fcastro.statement.config.category;

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
public class StatementConfigCategoryDto {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;
    private long statementConfigId;
    private String name;
    private String tags;
}
