package com.evgeniyfedorchenko.expAssistant.dto;

import com.evgeniyfedorchenko.expAssistant.entities.Category;

import java.time.ZonedDateTime;

public class TransactionInputDto {
    int accountTo;
    int accountFrom;
    CurrencyShortName currencyShortName;
    double sum;
    Category expenseCategory;
    ZonedDateTime dateTime;


}
