package com.evgeniyfedorchenko.expAssistant.client;

import com.evgeniyfedorchenko.expAssistant.enums.CurrencyShortName;
import com.evgeniyfedorchenko.expAssistant.exceptions.UnsupportedExchangeRateException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.client5.http.fluent.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * API для взаимодействия с сервером курсов обмена валют <a href="https://twelvedata.com">Twelve Data</a>
 */
@Component
public class TwelvedataClient {

    @Value("${treveldata.currency.rates.url}")
    private String url;
    @Value("${treveldata.currency.rates.api-key}")
    private String apiKey;

    private final Logger logger = LoggerFactory.getLogger(TwelvedataClient.class);


    /**
     * Метод для получения актуального курса валют с внешнего источника, данные для доступа к которому должны быть
     * определены в конфигурационном файле по шаблону типа A?symbol=B/C&apikey=D, где А - это протокол доменное имя и путь,
     * В - трехбуквенное наименование валюты продажи,
     * С - трехбуквенное наименование валюты покупки,
     * D - ключ для доступа к ресурсу (apikey)
     *
     * @param from трехбуквенное наименование валюты продажи
     * @param to   трехбуквенное наименование валюты покупки
     * @return актуальный курс обмена указанный валютной пары в типе BigDecimal
     * @throws UnsupportedExchangeRateException выбрасывается, если валютная пара (а так же ей обратная) не поддерживается указанным сервером
     */
    public BigDecimal getCurrencyRate(CurrencyShortName from, CurrencyShortName to) {
        logger.info("Start invoking to external rate source");
        String readyUrl = "%s?symbol=%s/%s&apikey=%s".formatted(url, from, to, apiKey);

        try {
            Response response = Request.get(readyUrl).execute();
            String result = response.returnContent().asString();
            boolean isInverted = false;

            /* Ресурс для получения курса может не поддерживать например KZT/USD, но поддерживать USD/KZT поэтому:
               запрашиваем курс, если его нет, то запрашиваем курс, ему обратный, и свапаем по формуле
                  KZT/USD = 1 / USD/KZT                                                    */

            if (result.startsWith("{\"code\":404")) {
                logger.debug("{} is unsupported, invoking for {}", MDC.get("pair"), from + "/" + to);
                readyUrl = "%s?symbol=%s/%s&apikey=%s".formatted(url, to, from, apiKey);
                response = Request.get(readyUrl).execute();
                result = response.returnContent().asString();
                isInverted = true;
            }
            if (result.startsWith("{\"code\":404")) {
                logger.warn("Rate {} and its inverted is not supported, throw exception", MDC.get("pair"));
                throw new UnsupportedExchangeRateException("%s/%s and its shifter is unsupported".formatted(from, to));
            }

            BigDecimal rateValue = getRateValue(result, isInverted);
            logger.info("End of invoke to external rate source, res: {}={}", MDC.get("pair"), rateValue);
            return rateValue;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private BigDecimal getRateValue(String result, boolean isInverted) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(result);
        BigDecimal rateVale = BigDecimal.valueOf(jsonNode.get("close").asDouble());
        logger.trace("Successfully read response from external source");

        return isInverted ? BigDecimal.ONE.divide(rateVale, 5, RoundingMode.HALF_EVEN) : rateVale;
    }
}
