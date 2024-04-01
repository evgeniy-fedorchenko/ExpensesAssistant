package com.evgeniyfedorchenko.expAssistant.repositories;

import com.evgeniyfedorchenko.expAssistant.entities.ExchangeRate;
import com.evgeniyfedorchenko.expAssistant.enums.CurrencyShortName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс для взаимодействия с таблицей, содержащей объекты ExchangeRate - обменные курсы валютных пар
 * */
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {

    /**
     * Метод для получения из базы данных объектов ExchangeRate, которые были рассчитаны вчера или ранее
     * @return List объектов ExchangeRate с устаревшей датой рассчета
     * */
    @Query(value = "SELECT * FROM expenses_rates WHERE calculation_date < CURRENT_DATE", nativeQuery = true)
    List<ExchangeRate> findOldRates();

    /**
     * Метод для поиска в базе данных объекта ExchangeRate по полному совпадению валютной пары
     * @param currencyFrom трехбуквенное наименование валютной пары ПРОДАЖИ
     * @param currencyTo трехбуквенное наименование валютной пары ПОКУПКИ
     * @return Optional.empty(), если такого объекта не найдено, Optional.of(ExchangeRate), если объект был найден
     * */
    Optional<ExchangeRate> findByCurrencyFromAndCurrencyTo(CurrencyShortName currencyFrom, CurrencyShortName currencyTo);
}
