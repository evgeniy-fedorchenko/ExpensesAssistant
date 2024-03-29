package com.evgeniyfedorchenko.expAssistant.services;

import com.evgeniyfedorchenko.expAssistant.client.TwelvedataClient;
import com.evgeniyfedorchenko.expAssistant.repositories.ExchangeRateRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;

@Service
public class ExchangeRateServiceImpl implements ExchangeRateService {

    private final TwelvedataClient client;
    private final ExchangeRateRepository exchangeRateRepository;

    public ExchangeRateServiceImpl(TwelvedataClient client,
                                   ExchangeRateRepository exchangeRateRepository) {
        this.client = client;
        this.exchangeRateRepository = exchangeRateRepository;
    }

    @PostConstruct
    public void init() {

        /* При запуске приложения проверяем на актуальность все курсы:
           берем курс, и если он обновлен раньше, чем сегодня в начале дня, то обновляем:
           через сеттеры ставим ему новое значение курса и новую дату обновления */

        exchangeRateRepository.findAll().stream()
                .filter(rate -> rate.getCalculationDate()
                        .isBefore(ChronoLocalDate.from(LocalDate.now().atStartOfDay())))
                .forEach(rate -> {
                    rate.setExchangeRate(client.getCurrencyRate(rate.getCurrencyFrom(), rate.getCurrencyTo()));
                    rate.setCalculationDate(LocalDate.now());
                    exchangeRateRepository.save(rate);
                });
    }


    @Override
    public String updateRubUsdRate() {
        return null;
    }

    @Override
    public String updateRubKztRate() {
        return null;
    }

    @Override
    public String updateKztUsdRate() {
        return null;
    }

    @Override
    public BigDecimal getUsdRubRate() {
        return null;
    }

    @Override
    public BigDecimal getRubKztRate() {
        return null;
    }

    @Override
    public BigDecimal getUsdKztRate() {
        return null;
    }
}
