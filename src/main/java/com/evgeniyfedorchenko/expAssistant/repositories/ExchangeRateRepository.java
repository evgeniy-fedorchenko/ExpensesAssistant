package com.evgeniyfedorchenko.expAssistant.repositories;

import com.evgeniyfedorchenko.expAssistant.entities.ExchangeRate;
import com.evgeniyfedorchenko.expAssistant.enums.CurrencyShortName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {

    @Query(value = "SELECT * FROM expenses_rates WHERE calculation_date < CURRENT_DATE", nativeQuery = true)
    List<ExchangeRate> findOldRates();

    Optional<ExchangeRate> findByCurrencyFromAndCurrencyTo(CurrencyShortName currencyFrom, CurrencyShortName currencyTo);
}
