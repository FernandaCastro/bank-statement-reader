package com.fcastro.statement;

import com.fcastro.statement.transaction.StatementTransaction;
import lombok.*;

import java.util.List;
import java.util.Map;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatementUpload {
    private Statement statement;
    private List<StatementTransaction> transactions;
    private Map<String, Double> summary;
}
