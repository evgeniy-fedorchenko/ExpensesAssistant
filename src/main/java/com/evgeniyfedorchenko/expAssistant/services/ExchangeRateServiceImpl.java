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

/**
 * Класс, содержащий логику получения актуальных курсов предусмотренных валют
 * */
@Service
public class ExchangeRateServiceImpl implements ExchangeRateService {

    private final TwelvedataClient client;

    private final ExchangeRateRepository exchangeRateRepository;

    public ExchangeRateServiceImpl(TwelvedataClient client,
                                   ExchangeRateRepository exchangeRateRepository) {
        this.client = client;
        this.exchangeRateRepository = exchangeRateRepository;
    }

    /**
     * PostContract-метод, выполняется при запуске приложения. Ищет устаревшие курсы валют (старше одного дня)
     * в таблице курсов валют и обновляет их
     * */
    @Override
    @PostConstruct
    public void init() {

        exchangeRateRepository.findOldRates()
                .forEach(rate -> {
                    rate.setExchangeRate(client.getCurrencyRate(rate.getCurrencyFrom(), rate.getCurrencyTo()));
                    rate.setCalculationDate(LocalDate.now());
                    exchangeRateRepository.save(rate);
                });
    }

    /**
     * Метод предоставляет актуальный курс валютной пары. Сначала проверяет наличие данного курса в собственной базе,
     * а так же его актуальность. Если курс не сохранен/устаревший, то запрашивает у
     * внешнего источника <a href="https://twelvedata.com/">Twelve Data</a>
     * @param currencyFrom трехбуквенное название валюты для ПРОДАЖИ
     * @param currencyTo трехбуквенное название валюты для ПОКУПКИ
     * @return объект BigDecimal формата %.5f - количество первой валюты в отношении второй
     * */
    @Override
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
