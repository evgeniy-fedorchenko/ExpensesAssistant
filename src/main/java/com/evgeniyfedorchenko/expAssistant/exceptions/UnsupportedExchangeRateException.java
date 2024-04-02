package com.evgeniyfedorchenko.expAssistant.exceptions;

/**
 * Исключение, которое будет сгенерировано, если обменный курс валютной пары,
 * а так же ей обратной, не найден на сайте <a href="https://twelvedata.com/">Twelve Data</a>
 */
public class UnsupportedExchangeRateException extends RuntimeException {

    public UnsupportedExchangeRateException(String message) {
        super(message);
    }
}
