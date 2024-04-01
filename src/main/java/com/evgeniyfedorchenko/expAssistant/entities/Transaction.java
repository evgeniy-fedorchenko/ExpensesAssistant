package com.evgeniyfedorchenko.expAssistant.entities;

import com.evgeniyfedorchenko.expAssistant.enums.Category;
import com.evgeniyfedorchenko.expAssistant.enums.CurrencyShortName;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    long accountTo;

    long accountFrom;

    @Column(columnDefinition = "DECIMAL(35,5)")
    @NotNull
    BigDecimal sum;

    CurrencyShortName currency;

    Category category;

    @NotNull
    ZonedDateTime dateTime;

    boolean limitExceeded;

    @ManyToOne
    @JoinColumn(name = "limit_id")
    Limit limit;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getAccountTo() {
        return accountTo;
    }

    public void setAccountTo(long accountTo) {
        this.accountTo = accountTo;
    }

    public long getAccountFrom() {
        return accountFrom;
    }

    public void setAccountFrom(long accountFrom) {
        this.accountFrom = accountFrom;
    }

    public BigDecimal getSum() {
        return sum;
    }

    public void setSum(BigDecimal sum) {
        this.sum = sum;
    }

    public CurrencyShortName getCurrency() {
        return currency;
    }

    public void setCurrency(CurrencyShortName currency) {
        this.currency = currency;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public ZonedDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(ZonedDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public boolean isLimitExceeded() {
        return limitExceeded;
    }

    public void setLimitExceeded(boolean limitExceeded) {
        this.limitExceeded = limitExceeded;
    }

    public Limit getLimit() {
        return limit;
    }

    public void setLimit(Limit limit) {
        this.limit = limit;
    }

    @Override
    public boolean equals(Object otherTransaction) {
        if (this == otherTransaction) {
            return true;
        }
        if (otherTransaction == null || getClass() != otherTransaction.getClass()) {
            return false;
        }
        Transaction transaction = (Transaction) otherTransaction;
        return id.equals(transaction.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Transaction{" +
               "id=" + id +
               ", accountTo=" + accountTo +
               ", accountFrom=" + accountFrom +
               ", sum=" + sum +
               ", currency=" + currency +
               ", category=" + category +
               ", dateTime=" + dateTime +
               ", limitExceeded=" + limitExceeded +
               '}';
    }
}
