package com.evgeniyfedorchenko.expAssistant.entities;

import com.evgeniyfedorchenko.expAssistant.enums.Category;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * Класс, представляющий сущность лимита устанавливаемого для объектов Transaction
 * Объекты сохраняются в таблице "limits". Имеет связь @OneToMany к сущности Transaction
 */
@Entity
@Table(name = "limits")
public class Limit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotNull
    Category forCategory;

    @PastOrPresent(message = "Transaction's date must be past or present")
    ZonedDateTime datetimeStarts;

    @Column(columnDefinition = "DECIMAL(35,5)")
    BigDecimal usdValue;

    @OneToMany(mappedBy = "limit", fetch = FetchType.EAGER)
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
     * Метод для добавления объекта Transaction в список List transactions
     * @param transaction объект Transaction, который нужно добавить
     *                    в коллекцию List transactions объекта this.Limit
     * @return - возвращает Limit с обновленными транзакциями (локально)
     */
    public Limit addTransaction(Transaction transaction) {
        if (transactions == null) {
            transactions = new ArrayList<>();
        }
        this.transactions.add(transaction);
        transaction.setLimit(this);
        return this;
    }

    /**
     * Рассчитывается на основе значения поля id
     */
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

    /**
     * Рассчитывается на основе значения поля id
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "{id=" + id +
               ", forCategory=" + forCategory +
               ", datetimeStarts=" + datetimeStarts +
               ", usdValue=" + usdValue + "}";
    }
}
