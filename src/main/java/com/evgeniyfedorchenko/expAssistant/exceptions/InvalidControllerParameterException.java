package com.evgeniyfedorchenko.expAssistant.exceptions;

public class InvalidControllerParameterException extends RuntimeException {

    public InvalidControllerParameterException(String message, Throwable cause) {
        super(message, cause);
    }
}
