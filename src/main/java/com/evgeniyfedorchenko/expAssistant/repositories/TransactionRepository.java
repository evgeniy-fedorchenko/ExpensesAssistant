package com.evgeniyfedorchenko.expAssistant.repositories;

import com.evgeniyfedorchenko.expAssistant.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Интерфейс для взаимодействия с таблицей, содержащей объекты Transaction - совершенные денежные транзакции
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Метод для получения информации о транзакциях, совершенных после (или во время) превышения лимита,
     * установленного на момент регистрации транзакции
     * @return Список массивов объектов, содержащих всю информацию о таких транзакциях.
     *         Каждый массив объектов типа Object - набор значений соответствующих полей транзакции или лимита,
     *         превышенного этой транзакцией. Рекомендуется использовать маппер для преобразования ответа в список объектов
     *         TransactionOverLimitDto, например TransactionOverLimitMapper и его метод toDto(Object[])
     */
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
