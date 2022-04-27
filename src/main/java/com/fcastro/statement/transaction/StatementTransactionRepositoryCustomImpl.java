package com.fcastro.statement.transaction;

import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.MultiValueMap;

import javax.persistence.EntityManager;
import javax.persistence.Parameter;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class StatementTransactionRepositoryCustomImpl implements StatementTransactionRepositoryCustom {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatementTransactionRepositoryCustomImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<StatementTransaction> findAllByStatementIdAndAllParams(Long statementId, MultiValueMap<String, String> params) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<StatementTransaction> query = cb.createQuery(StatementTransaction.class);
        Root<StatementTransaction> transaction = query.from(StatementTransaction.class);

        List<Predicate> predicates = buildCriteria(statementId, params, transaction, cb);

        query.select(transaction)
                .where(cb.and(predicates.toArray(new Predicate[predicates.size()])));

        var executableQuery = entityManager.createQuery(query);
        logQuery(executableQuery);
        return executableQuery.getResultList();
    }

    private List<Predicate> buildCriteria(Long statementId, MultiValueMap<String, String> params, Root<StatementTransaction> transaction, CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(transaction.<Long>get("statementId"), statementId));

        if (params == null || params.isEmpty()) {
            return predicates;
        }

        List<String> descriptions = params.get("description");
        if (descriptions != null) {
            List<Predicate> descriptionPredicates = new ArrayList<>();
            descriptions.forEach(description ->
                    descriptionPredicates.add(
                            cb.like(cb.upper(transaction.<String>get("description")),
                                    Strings.concat(description, "%").toUpperCase())
                    ));
            predicates.add(cb.and(cb.or(descriptionPredicates.toArray(new Predicate[descriptionPredicates.size()]))));
        }

        List<String> documentIds = params.get("documentId");
        if (documentIds != null) {
            CriteriaBuilder.In<String> inClauseDocumentId = cb.in(cb.upper(transaction.<String>get("documentId")));
            documentIds.forEach(documentId -> inClauseDocumentId.value(documentId.toUpperCase()));
            predicates.add(inClauseDocumentId);
        }

        List<String> categories = params.get("category");
        if (categories != null) {
            CriteriaBuilder.In<String> inClauseCategory = cb.in(cb.upper(transaction.<String>get("category")));
            categories.forEach(category -> inClauseCategory.value(category.toUpperCase()));
            predicates.add(inClauseCategory);
        }

        List<String> transactionDates = params.get("transactionDate");
        if (transactionDates != null) {
            CriteriaBuilder.In<String> inClauseTransactionDate = cb.in(cb.upper(transaction.<String>get("transactionDate")));
            transactionDates.forEach((transactionDate) -> inClauseTransactionDate.value(transactionDate.toUpperCase()));
            predicates.add(inClauseTransactionDate);
        }

        List<String> transactionValues = params.get("transactionValue");
        if (transactionValues != null) {
            List<Predicate> TransactionValuePredicates = new ArrayList<>();
            transactionValues.forEach((transactionValue) -> TransactionValuePredicates.add(cb.equal(transaction.<Double>get("transactionValue"), transactionValue)));
            predicates.add(cb.and(cb.or(TransactionValuePredicates.toArray(new Predicate[TransactionValuePredicates.size()]))));
        }
        return predicates;
    }

    private void logQuery(TypedQuery<StatementTransaction> query) {

        LOGGER.info(query.unwrap(org.hibernate.query.Query.class).getQueryString());

        query.unwrap(org.hibernate.query.Query.class).getParameters().stream()
                .sorted(Comparator.comparing(Parameter::getName))
                .forEach(parameter -> {
                    LOGGER.info(parameter.getName() + ": " +
                    query.unwrap(org.hibernate.query.Query.class).getParameterValue(parameter.getName()) + " ");
                });
    }
}
