package com.fcastro.statement.transaction;

import java.util.List;
import java.util.Map;

public interface StatementTransactionRepositoryCustom {

    List<StatementTransaction> findAllByStatementIdAndAllParams(Long statementId, Map<String, String> params);
}
