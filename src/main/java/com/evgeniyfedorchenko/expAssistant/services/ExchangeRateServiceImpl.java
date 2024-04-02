package com.evgeniyfedorchenko.expAssistant.services;

import com.evgeniyfedorchenko.expAssistant.client.TwelvedataClient;
import com.evgeniyfedorchenko.expAssistant.entities.ExchangeRate;
import com.evgeniyfedorchenko.expAssistant.enums.CurrencyShortName;
import com.evgeniyfedorchenko.expAssistant.repositories.ExchangeRateRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Класс, содержащий логику получения актуальных курсов предусмотренных валют
 */
@Service
public class ExchangeRateServiceImpl implements ExchangeRateService {

    private final TwelvedataClient client;
    private final ExchangeRateRepository exchangeRateRepository;
    private final Logger logger = LoggerFactory.getLogger(ExchangeRateServiceImpl.class);

    public ExchangeRateServiceImpl(TwelvedataClient client,
                                   ExchangeRateRepository exchangeRateRepository) {
        this.client = client;
        this.exchangeRateRepository = exchangeRateRepository;
    }

    /**
     * PostContract-метод, выполняется при запуске приложения. Ищет устаревшие курсы валют (старше одного дня)
     * в таблице курсов валют и обновляет их
     */
    @Override
    @PostConstruct
    public void init() {

        Stream<ExchangeRate> oldRates = exchangeRateRepository.findOldRates().stream()
                .peek(rate -> {
                    rate.setExchangeRate(client.getCurrencyRate(rate.getCurrencyFrom(), rate.getCurrencyTo()));
                    rate.setCalculationDate(LocalDate.now());
                    exchangeRateRepository.save(rate);
                });
        logger.info("Was updated {} rates by init()", oldRates.count());
    }

    /**
     * Метод предоставляет актуальный курс валютной пары. Сначала проверяет наличие данного курса в собственной базе,
     * а так же его актуальность. Если курс не сохранен/устаревший, то запрашивает у
     * внешнего источника <a href="https://twelvedata.com/">Twelve Data</a>
     *
     * @param currencyFrom трехбуквенное название валюты для ПРОДАЖИ
     * @param currencyTo   трехбуквенное название валюты для ПОКУПКИ
     * @return объект BigDecimal формата %.5f - количество первой валюты в отношении второй
     */
    @Override
    public BigDecimal getExchangeRate(CurrencyShortName currencyFrom, CurrencyShortName currencyTo) {
        Optional<ExchangeRate> rateOpt = exchangeRateRepository.findByCurrencyFromAndCurrencyTo(currencyFrom, currencyTo);
        MDC.put("pair", currencyFrom + "/" + currencyTo);
        if (rateOpt.isEmpty()) {
            logger.info("Rate {} not found in DB, invoking external source", MDC.get("pair"));
            return createExchangeRate(currencyFrom, currencyTo);

        } else if (!rateOpt.get().getCalculationDate().equals(LocalDate.now())) {
            logger.info("Rate {} is too old, invoking external source", MDC.get("pair"));
            return updateExchangeRate(rateOpt.get());

        } else {
            logger.info("Actual rate {} was found", MDC.get("pair"));
            MDC.remove("pair");
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
            ExchangeRate savedRate = exchangeRateRepository.save(exchangeRate);
            logger.info("Successfully saved new ExchangeRate {}", savedRate);
        });
        savingThread.start();
        logger.info("Returned new rate, value={}", actualCurrencyRate.toString());
        return actualCurrencyRate;

    }

    private BigDecimal updateExchangeRate(ExchangeRate rate) {
        BigDecimal actualCurrencyRate = client.getCurrencyRate(rate.getCurrencyFrom(), rate.getCurrencyTo());

        Thread savingThread = new Thread(() -> {
            rate.setExchangeRate(actualCurrencyRate);
            rate.setCalculationDate(LocalDate.now());
            ExchangeRate savedRate = exchangeRateRepository.save(rate);
            logger.info("Successfully saved new ExchangeRate {}", savedRate);

        });
        savingThread.start();
        logger.info("Returned updated rate, value={}", actualCurrencyRate.toString());
        return rate.getExchangeRate();
    }
}
