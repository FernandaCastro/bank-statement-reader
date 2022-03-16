package com.fcastro.statement;

import com.fcastro.statement.transaction.StatementTransaction;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter @Setter
@Builder(toBuilder = true)
public class StatementUploadResponse {
    private Statement statement;
    private List<StatementTransaction> transactions;
    private Map<String, Double> summary;
}
