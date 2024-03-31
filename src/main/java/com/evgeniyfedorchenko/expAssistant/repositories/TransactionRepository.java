package com.evgeniyfedorchenko.expAssistant.repositories;

import com.evgeniyfedorchenko.expAssistant.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query(value = """
            SELECT
                trscn.account_to AS account_to,
                trscn.currency AS trscn_currency,
                trscn.sum AS trscn_sum,
                trscn.category AS expense_category,
                trscn.date_time AS trscn_do_date_time,
                lmt.usd_value AS lmt_value,
                lmt.datetime_starts AS datetime_starts
            FROM
                transactions trscn
            INNER JOIN
                limits lmt ON trscn.limit_id = lmt.id
            WHERE
                trscn.limit_exceeded = true;""", nativeQuery = true)
    List<Object[]> findOverLimitTransactions();
}
