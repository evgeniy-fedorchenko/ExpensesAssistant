package com.evgeniyfedorchenko.expAssistant.dto;

import com.evgeniyfedorchenko.expAssistant.enums.Category;
import com.evgeniyfedorchenko.expAssistant.enums.CurrencyShortName;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * Класс, представляющий объект, содержащий информацию о транзакциях и их лимитах
 */
public class TransactionOverLimitDto {

    private long accountFrom;
    private long accountTo;
    private CurrencyShortName trscnCurrency;
    private BigDecimal transactionSum;
    private Category expenseCategory;
    private ZonedDateTime transactionDateTime;
    private BigDecimal limitValue;
    private ZonedDateTime limitDatetimeStarts;
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

    public CurrencyShortName getTrscnCurrency() {
        return trscnCurrency;
    }

    public void setTrscnCurrency(CurrencyShortName trscnCurrency) {
        this.trscnCurrency = trscnCurrency;
    }

    public BigDecimal getTransactionSum() {
        return transactionSum;
    }

    public void setTransactionSum(BigDecimal transactionSum) {
        this.transactionSum = transactionSum;
    }

    public Category getExpenseCategory() {
        return expenseCategory;
    }

    public void setExpenseCategory(Category expenseCategory) {
        this.expenseCategory = expenseCategory;
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

    public ZonedDateTime getLimitDatetimeStarts() {
        return limitDatetimeStarts;
    }

    public void setLimitDatetimeStarts(ZonedDateTime limitDatetimeStarts) {
        this.limitDatetimeStarts = limitDatetimeStarts;
    }

    public CurrencyShortName getLimitCurrency() {
        return limitCurrency;
    }

    public void setLimitCurrency(CurrencyShortName limitCurrency) {
        this.limitCurrency = limitCurrency;
    }
}
