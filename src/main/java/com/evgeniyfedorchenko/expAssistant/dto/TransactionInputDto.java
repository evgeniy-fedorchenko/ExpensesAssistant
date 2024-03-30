package com.evgeniyfedorchenko.expAssistant.dto;

import com.evgeniyfedorchenko.expAssistant.enums.Category;
import com.evgeniyfedorchenko.expAssistant.enums.CurrencyShortName;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;

import java.time.ZonedDateTime;

@Validated
public class TransactionInputDto {

    /* Принимаем строку, так как по условию это "Целочисленный тип данных, 10 знаков", но нельзя проверить это
       аннотацией @Max, тк её параметр value имеет тип Integer, в который не влазит 9_999_999_999 (10 цифр) */
    @Pattern(regexp = "^\\d{10}$", message = "Invalid counterpart's account")
    String accountTo;

    @Nullable
    String accountFrom;   // Переводить деньги можно только со своего счета, так что этот параметр всегда одинаков

    @NotNull(message = "Currency can not be empty")
    CurrencyShortName currency;

//    @Min(value = 12, message = "min annotation")
    @Positive(message = "Sum must be positive")
    Double sum;

    @NotNull(message = "Category can not be empty")
    Category expenseCategory;

    // TODO: 30.03.2024 Раскомментить перед сдачей
//    @PastOrPresent(message = "Transaction's date must be past or present")
    ZonedDateTime dateTime;

    public String getAccountTo() {
        return accountTo;
    }

    public void setAccountTo(String accountTo) {
        this.accountTo = accountTo;
    }

    public String getAccountFrom() {
        return accountFrom;
    }

    public void setAccountFrom(String accountFrom) {
        this.accountFrom = accountFrom;
    }

    public CurrencyShortName getCurrency() {
        return currency;
    }

    public void setCurrency(CurrencyShortName currency) {
        this.currency = currency;
    }

    public Double getSum() {
        return sum;
    }

    public void setSum(Double sum) {
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
