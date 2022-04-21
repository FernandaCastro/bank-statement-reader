package com.fcastro.statement;

import com.fcastro.statement.transaction.StatementTransactionView;
import lombok.*;

import java.util.List;
import java.util.Map;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class StatementUploadView {
    private StatementView statement;
    private List<StatementTransactionView> transactions;
    private Map<String, Double> summary;
}
