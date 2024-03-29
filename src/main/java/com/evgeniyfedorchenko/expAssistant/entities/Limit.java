package com.evgeniyfedorchenko.expAssistant.entities;

import com.evgeniyfedorchenko.expAssistant.enums.Category;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "limits")
public class Limit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    Category forCategory;

    ZonedDateTime datetimeStarts;

    BigDecimal value;

    @OneToMany(mappedBy = "limit")
    List<Transaction> transactions;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Category getForCategory() {
        return forCategory;
    }

    public void setForCategory(Category forCategory) {
        this.forCategory = forCategory;
    }

    public ZonedDateTime getDatetimeStarts() {
        return datetimeStarts;
    }

    public void setDatetimeStarts(ZonedDateTime datetimeStarts) {
        this.datetimeStarts = datetimeStarts;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    @Override
    public boolean equals(Object otherLimit) {
        if (this == otherLimit) {
            return true;
        }
        if (otherLimit == null || getClass() != otherLimit.getClass()) {
            return false;
        }
        Limit limit = (Limit) otherLimit;
        return id.equals(limit.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
