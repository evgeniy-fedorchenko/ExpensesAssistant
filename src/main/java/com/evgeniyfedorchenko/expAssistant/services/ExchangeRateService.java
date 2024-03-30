package com.evgeniyfedorchenko.expAssistant.services;

import com.evgeniyfedorchenko.expAssistant.client.TwelvedataClient;
import com.evgeniyfedorchenko.expAssistant.entities.ExchangeRate;
import com.evgeniyfedorchenko.expAssistant.enums.CurrencyShortName;
import com.evgeniyfedorchenko.expAssistant.repositories.ExchangeRateRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class ExchangeRateService {

    private final TwelvedataClient client;
    private final ExchangeRateRepository exchangeRateRepository;

    public ExchangeRateService(TwelvedataClient client,
                               ExchangeRateRepository exchangeRateRepository) {
        this.client = client;
        this.exchangeRateRepository = exchangeRateRepository;
    }

    @PostConstruct
    public void init() {

        /* При запуске приложения проверяем на актуальность все курсы:
           берем курс, и если он обновлен раньше, чем сегодня в начале дня, то обновляем:
           через сеттеры ставим ему новое значение курса и новую дату обновления */

        exchangeRateRepository.findOldRates()
                .forEach(rate -> {
                    rate.setExchangeRate(client.getCurrencyRate(rate.getCurrencyFrom(), rate.getCurrencyTo()));
                    rate.setCalculationDate(LocalDate.now());
                    exchangeRateRepository.save(rate);
                });
    }

    /* Получаем курс, если его нет - получаем на бирже и сохраняем в бд
       иначе если он есть, но неактуальный - получаем на бирже актуальный, обновляем в бд и возвращаем
       иначе (если он есть и актуальный) - просто возвращаем */
    public BigDecimal getExchangeRate(CurrencyShortName currencyFrom, CurrencyShortName currencyTo) {
        Optional<ExchangeRate> rateOpt = exchangeRateRepository.findByCurrencyFromAndCurrencyTo(currencyFrom, currencyTo);

        if (rateOpt.isEmpty()) {
            return createExchangeRate(currencyFrom, currencyTo);

        } else if (!rateOpt.get().getCalculationDate().equals(LocalDate.now())) {
            return updateExchangeRate(rateOpt.get());

        } else {
            return rateOpt.get().getExchangeRate();
        }
    }

    private BigDecimal createExchangeRate(CurrencyShortName currencyFrom, CurrencyShortName currencyTo) {
        BigDecimal actualCurrencyRate = client.getCurrencyRate(currencyFrom, currencyTo);

        Thread savingThread = new Thread(() -> {
            ExchangeRate exchangeRate = new ExchangeRate();
            exchangeRate.setCurrencyFrom(currencyFrom);
            exchangeRate.setCurrencyTo(currencyTo);
            exchangeRate.setExchangeRate(actualCurrencyRate);
            exchangeRate.setCalculationDate(LocalDate.now());
            exchangeRateRepository.save(exchangeRate);
        });
        savingThread.start();
        return actualCurrencyRate;

    }

    private BigDecimal updateExchangeRate(ExchangeRate rate) {
        BigDecimal actualCurrencyRate = client.getCurrencyRate(rate.getCurrencyFrom(), rate.getCurrencyTo());

        Thread savingThread = new Thread(() -> {
            rate.setExchangeRate(actualCurrencyRate);
            rate.setCalculationDate(LocalDate.now());
            exchangeRateRepository.save(rate);
        });
        savingThread.start();

        return rate.getExchangeRate();
    }
}
