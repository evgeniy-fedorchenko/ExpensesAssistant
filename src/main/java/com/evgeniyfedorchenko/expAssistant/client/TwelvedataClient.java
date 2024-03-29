package com.evgeniyfedorchenko.expAssistant.client;

import com.evgeniyfedorchenko.expAssistant.dto.CurrencyShortName;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TwelvedataClient {

    private final OkHttpClient client;

    @Value("${treveldata.currency.rates.url}")
    private String url;
    @Value("${treveldata.currency.rates.api-key}")
    private String apiKey;


    public TwelvedataClient(OkHttpClient client) {
        this.client = client;
    }

    public String getCurrencyRate(CurrencyShortName from, CurrencyShortName to) {
        return null;
    }
}
