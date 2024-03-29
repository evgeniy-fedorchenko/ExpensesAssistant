package com.evgeniyfedorchenko.expAssistant.client;

import com.evgeniyfedorchenko.expAssistant.enums.CurrencyShortName;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.client5.http.fluent.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;

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

    public BigDecimal getCurrencyRate(CurrencyShortName from, CurrencyShortName to) {

        String readyUrl = "%s?symbol=%s/%s&apikey=%s".formatted(url, from, to, apiKey);
        String result;
        try {
            Response response = Request.get(readyUrl).execute();
            result = response.returnContent().asString();
            return getRateValue(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private BigDecimal getRateValue(String result) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(result);

        return BigDecimal.valueOf(jsonNode.get("rate").asDouble());
    }
}
