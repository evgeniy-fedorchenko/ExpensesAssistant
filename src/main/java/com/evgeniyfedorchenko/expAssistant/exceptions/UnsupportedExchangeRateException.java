package com.evgeniyfedorchenko.expAssistant.exceptions;

public class UnsupportedExchangeRateException extends RuntimeException {

    public UnsupportedExchangeRateException(String message) {
        super(message);
    }
}
