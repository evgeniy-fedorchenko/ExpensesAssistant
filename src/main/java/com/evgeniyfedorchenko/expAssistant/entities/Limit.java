package com.evgeniyfedorchenko.expAssistant.entities;

import com.evgeniyfedorchenko.expAssistant.enums.Category;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
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

    @Column(columnDefinition = "DECIMAL(35,5)")
    BigDecimal usdValue;

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

    public BigDecimal getUsdValue() {
        return usdValue;
    }

    public void setUsdValue(BigDecimal usdValue) {
        this.usdValue = usdValue;
    }

    public List<Transaction> getTransactions() {
        return transactions == null ? new ArrayList<>() : transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    /**
     * @param transaction объект Transaction, который нужно добавить
     *                    в коллекцию List<Transaction> transactions объекта this.Limit
     * @return - возвращает Limit с обновленными транзакциями (локально)
     */
    public Limit addTransaction(Transaction transaction) {
        this.getTransactions().add(transaction);
        transaction.setLimit(this);   // TODO: 30.03.2024 Нужна ли эта строчка?
        return this;
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
