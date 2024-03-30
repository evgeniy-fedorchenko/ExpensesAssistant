package com.evgeniyfedorchenko.expAssistant.dto;

import com.evgeniyfedorchenko.expAssistant.enums.Category;
import com.evgeniyfedorchenko.expAssistant.enums.CurrencyShortName;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public class TransactionOverLimitDto {

    private long accountFrom;
    private long accountTo;
    private CurrencyShortName currency;
    private BigDecimal transactionSum;
    private Category category;
    private ZonedDateTime transactionDateTime;
    private BigDecimal limitValue;
    private ZonedDateTime limitDateTimeStarts;
    private CurrencyShortName limitCurrency;


    public long getAccountFrom() {
        return accountFrom;
    }

    public void setAccountFrom(long accountFrom) {
        this.accountFrom = accountFrom;
    }

    public long getAccountTo() {
        return accountTo;
    }

    public void setAccountTo(long accountTo) {
        this.accountTo = accountTo;
    }

    public CurrencyShortName getCurrency() {
        return currency;
    }

    public void setCurrency(CurrencyShortName currency) {
        this.currency = currency;
    }

    public BigDecimal getTransactionSum() {
        return transactionSum;
    }

    public void setTransactionSum(BigDecimal transactionSum) {
        this.transactionSum = transactionSum;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public ZonedDateTime getTransactionDateTime() {
        return transactionDateTime;
    }

    public void setTransactionDateTime(ZonedDateTime transactionDateTime) {
        this.transactionDateTime = transactionDateTime;
    }

    public BigDecimal getLimitValue() {
        return limitValue;
    }

    public void setLimitValue(BigDecimal limitValue) {
        this.limitValue = limitValue;
    }

    public ZonedDateTime getLimitDateTimeStarts() {
        return limitDateTimeStarts;
    }

    public void setLimitDateTimeStarts(ZonedDateTime limitDateTimeStarts) {
        this.limitDateTimeStarts = limitDateTimeStarts;
    }

    public CurrencyShortName getLimitCurrency() {
        return limitCurrency;
    }

    public void setLimitCurrency(CurrencyShortName limitCurrency) {
        this.limitCurrency = limitCurrency;
    }
}
