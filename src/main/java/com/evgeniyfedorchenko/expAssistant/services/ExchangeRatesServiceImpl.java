package com.evgeniyfedorchenko.expAssistant.services;

import com.evgeniyfedorchenko.expAssistant.client.TwelvedataClient;
import org.springframework.stereotype.Service;

@Service
public class ExchangeRatesServiceImpl implements ExchangeRatesService {

    private final TwelvedataClient client;

    public ExchangeRatesServiceImpl(TwelvedataClient client) {
        this.client = client;
    }

    @Override
    public String getRUBExchangeRate() {
        return null;
    }

    @Override
    public String getKZTExchangeRate() {
        return null;
    }
}
