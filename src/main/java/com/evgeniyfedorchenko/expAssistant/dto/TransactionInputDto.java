package com.evgeniyfedorchenko.expAssistant.dto;

import com.evgeniyfedorchenko.expAssistant.enums.Category;
import com.evgeniyfedorchenko.expAssistant.enums.CurrencyShortName;

import java.time.ZonedDateTime;

public class TransactionInputDto {

    int accountTo;

    int accountFrom;

    CurrencyShortName currency;

    double sum;

    Category expenseCategory;

    ZonedDateTime dateTime;


    public int getAccountTo() {
        return accountTo;
    }

    public void setAccountTo(int accountTo) {
        this.accountTo = accountTo;
    }

    public int getAccountFrom() {
        return accountFrom;
    }

    public void setAccountFrom(int accountFrom) {
        this.accountFrom = accountFrom;
    }

    public CurrencyShortName getCurrency() {
        return currency;
    }

    public void setCurrency(CurrencyShortName currency) {
        this.currency = currency;
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    public Category getExpenseCategory() {
        return expenseCategory;
    }

    public void setExpenseCategory(Category expenseCategory) {
        this.expenseCategory = expenseCategory;
    }

    public ZonedDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(ZonedDateTime dateTime) {
        this.dateTime = dateTime;
    }
}
