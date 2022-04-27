package com.fcastro.statement.transaction;

import org.springframework.util.MultiValueMap;

import java.util.List;

public interface StatementTransactionRepositoryCustom {

    List<StatementTransaction> findAllByStatementIdAndAllParams(Long statementId, MultiValueMap<String, String> params);
}
