package com.evgeniyfedorchenko.expAssistant.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    Category category;

    BigDecimal dollarSum;

    ZonedDateTime dateTime;

    @ManyToOne
    @JoinColumn(name = "limit_id")
    Limit limit;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public BigDecimal getDollarSum() {
        return dollarSum;
    }

    public void setDollarSum(BigDecimal dollarSum) {
        this.dollarSum = dollarSum;
    }

    public ZonedDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(ZonedDateTime dateTime) {
        this.dateTime = dateTime;
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
}
