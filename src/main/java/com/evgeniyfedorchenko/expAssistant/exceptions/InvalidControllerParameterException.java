package com.evgeniyfedorchenko.expAssistant.exceptions;

/**
 * Исключение, которое будет сгенерировано при обнаружении невалидного параметра, пришедшего из контроллера.
 * Необходимо для отлова невалидных параметров, пропущенных Spring Validator
 */
public class InvalidControllerParameterException extends RuntimeException {

    public InvalidControllerParameterException(String message, Throwable cause) {
        super(message, cause);
    }
}
