package com.evgeniyfedorchenko.expAssistant.services;

import com.evgeniyfedorchenko.expAssistant.entities.Limit;
import com.evgeniyfedorchenko.expAssistant.enums.Category;

import java.math.BigDecimal;
import java.util.Optional;

public interface LimitService {
    Optional<Limit> findLastLimit();

    Limit createNewDefaultLimit(Category category);

    void createNewCustomLimit(Category forCategory, BigDecimal value);
}
