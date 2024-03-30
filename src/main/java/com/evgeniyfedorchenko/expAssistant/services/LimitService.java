package com.evgeniyfedorchenko.expAssistant.services;

import com.evgeniyfedorchenko.expAssistant.entities.Limit;
import com.evgeniyfedorchenko.expAssistant.entities.Transaction;
import com.evgeniyfedorchenko.expAssistant.enums.Category;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

public interface LimitService {
    Optional<Limit> findLastLimit();

    Limit createNewDefaultLimit(Category category);

    void createNewCustomLimit(Category forCategory, BigDecimal value);

    void addTransaction(Transaction newTransaction, Limit actualLimit);

    default ZonedDateTime getStartOfMonth() {
        Instant now = Instant.now();
        return now.atZone(ZoneId.of("Europe/Moscow"))
                .withDayOfMonth(1)
                .with(LocalTime.MIN);
    }
}
