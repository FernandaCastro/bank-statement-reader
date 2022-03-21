package com.fcastro.statement.config;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatementConfigRepository extends JpaRepository<StatementConfig, Long> {
    StatementConfig findByBankIdAndClientId(long bankId, long clientId);

    List<StatementConfig> findAllByClientId(long clientId);
}
