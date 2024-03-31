package com.evgeniyfedorchenko.expAssistant.client;

import com.evgeniyfedorchenko.expAssistant.enums.CurrencyShortName;
import com.evgeniyfedorchenko.expAssistant.exceptions.UnsupportedExchangeRateException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.client5.http.fluent.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class TwelvedataClient {

    @Value("${treveldata.currency.rates.url}")
    private String url;
    @Value("${treveldata.currency.rates.api-key}")
    private String apiKey;


    /* Данный в задании ресурс для получения курса может не поддерживать например KZT/USD, но поддерживать USD/KZT поэтому:
       запрашиваем курс, если его нет, то запрашиваем курс, ему обратный, и свапаем по формуле
          rete = 1 / invertedRate
       после чего возвращаем либо кидаем UnsupportedExchangeRateException, если таких курсов нет */
    public BigDecimal getCurrencyRate(CurrencyShortName from, CurrencyShortName to) {

        String readyUrl = "%s?symbol=%s/%s&apikey=%s".formatted(url, from, to, apiKey);

        try {
            Response response = Request.get(readyUrl).execute();
            String result = response.returnContent().asString();
            boolean isInverted = false;

            if (result.startsWith("{\"code\":404")) {
                readyUrl = "%s?symbol=%s/%s&apikey=%s".formatted(url, to, from, apiKey);
                response = Request.get(readyUrl).execute();
                result = response.returnContent().asString();
                isInverted = true;
            }
            if (result.startsWith("{\"code\":404")) {
                throw new UnsupportedExchangeRateException("%s/%s and its shifter is unsupported".formatted(from, to));
            }

            return getRateValue(result, isInverted);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private BigDecimal getRateValue(String result, boolean isInverted) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(result);
        BigDecimal rateVale = BigDecimal.valueOf(jsonNode.get("close").asDouble());

        return isInverted ? BigDecimal.ONE.divide(rateVale, 5, RoundingMode.HALF_EVEN) : rateVale;
    }
}
