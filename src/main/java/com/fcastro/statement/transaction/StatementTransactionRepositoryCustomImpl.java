package com.fcastro.statement.transaction;

import org.apache.logging.log4j.util.Strings;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StatementTransactionRepositoryCustomImpl implements StatementTransactionRepositoryCustom{
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<StatementTransaction> findAllByStatementIdAndAllParams(Long statementId, Map<String, String> params){

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<StatementTransaction> query = cb.createQuery(StatementTransaction.class);
        Root<StatementTransaction>  transaction = query.from(StatementTransaction.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(transaction.<Long>get("statementId"), statementId));

        if (params != null && !params.isEmpty()) {

            String description = params.get("description");
            if (Strings.isNotBlank(description)) {
                predicates.add(cb.like(transaction.<String>get("description"), Strings.concat(description, "%")));
            }

            String documentId = params.get("documentId");
            if (Strings.isNotBlank(documentId)) {
                predicates.add(cb.equal(transaction.<String>get("documentId"), documentId));
            }

            String category = params.get("category");
            if (Strings.isNotBlank(category)) {
                predicates.add(cb.equal(transaction.<String>get("category"), category));
            }

            String transactionDate = params.get("transactionDate");
            if (Strings.isNotBlank(transactionDate)) {
                predicates.add(cb.equal(transaction.<String>get("transactionDate"), transactionDate));
            }

            String transactionValue = params.get("transactionValue");
            if (Strings.isNotBlank(transactionValue)) {
                predicates.add(cb.equal(transaction.<Double>get("transactionValue"), transactionValue));
            }
        }

        query.select(transaction)
                .where(cb.and(predicates.toArray(new Predicate[predicates.size()])));

        return entityManager.createQuery(query)
                .getResultList();
    }
}
