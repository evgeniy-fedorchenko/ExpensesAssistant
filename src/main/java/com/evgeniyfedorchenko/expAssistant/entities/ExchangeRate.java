package com.evgeniyfedorchenko.expAssistant.entities;


import com.evgeniyfedorchenko.expAssistant.enums.CurrencyShortName;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "expenses_rates")
public class ExchangeRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    CurrencyShortName currencyFrom;

    CurrencyShortName currencyTo;

    BigDecimal exchangeRate;

    LocalDate calculationDate;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CurrencyShortName getCurrencyFrom() {
        return currencyFrom;
    }

    public void setCurrencyFrom(CurrencyShortName currencyFrom) {
        this.currencyFrom = currencyFrom;
    }

    public CurrencyShortName getCurrencyTo() {
        return currencyTo;
    }

    public void setCurrencyTo(CurrencyShortName currencyTo) {
        this.currencyTo = currencyTo;
    }

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public LocalDate getCalculationDate() {
        return calculationDate;
    }

    public void setCalculationDate(LocalDate calculationDate) {
        this.calculationDate = calculationDate;
    }

    @Override
    public boolean equals(Object otherExpensesRate) {
        if (this == otherExpensesRate) {
            return true;
        }
        if (otherExpensesRate == null || getClass() != otherExpensesRate.getClass()) {
            return false;
        }
        ExchangeRate that = (ExchangeRate) otherExpensesRate;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
