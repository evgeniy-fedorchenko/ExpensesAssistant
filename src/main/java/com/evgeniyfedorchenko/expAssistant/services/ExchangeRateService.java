package com.evgeniyfedorchenko.expAssistant.services;

import com.evgeniyfedorchenko.expAssistant.enums.CurrencyShortName;
import jakarta.annotation.PostConstruct;

import java.math.BigDecimal;

public interface ExchangeRateService {
    @PostConstruct
    void init();

    BigDecimal getExchangeRate(CurrencyShortName currencyFrom, CurrencyShortName currencyTo);
}
