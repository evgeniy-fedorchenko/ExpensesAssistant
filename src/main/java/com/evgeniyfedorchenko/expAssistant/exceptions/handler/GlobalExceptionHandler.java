package com.evgeniyfedorchenko.expAssistant.exceptions.handler;

import com.evgeniyfedorchenko.expAssistant.exceptions.InvalidControllerParameterException;
import com.evgeniyfedorchenko.expAssistant.exceptions.UnsupportedExchangeRateException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.IntStream;

@RestControllerAdvice
public class GlobalExceptionHandler {

    Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(DataIntegrityViolationException.class)     // Нарушение целостности БД (нарушение констрейнтов)
    public ResponseEntity<String> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)    // Невалидные параметры inputDto
    public ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {

        StringBuilder errMessBuilder = new StringBuilder("Validation errors:\n");
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        IntStream.range(0, fieldErrors.size())
                .forEach(i -> errMessBuilder.append(i + 1)
                        .append(". ")
                        .append(fieldErrors.get(i).getField())
                        .append("\n")
                );
        ResponseEntity<String> body = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errMessBuilder.toString());
        logger.debug("In inputDto were found {} invalidParams, response body = {}", fieldErrors.size(), body.getBody());
        return body;
    }

    @ExceptionHandler(InvalidControllerParameterException.class)
    public ResponseEntity<String> handleInvalidControllerParameterException(InvalidControllerParameterException e) {
        logger.debug("handle {}", e.getClass().getSimpleName());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
    // Эти два метода хендлера не объединены, чтобы можно было из параметра получить имя класса исключения для логирования
    @ExceptionHandler(UnsupportedExchangeRateException.class)
    public ResponseEntity<String> handleUnsupportedExchangeRateExceptionException(UnsupportedExchangeRateException e) {
        logger.debug("handle {}", e.getClass().getSimpleName());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)    // Невалидные значения параметров пути в контроллере
    public ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e) {

        StringBuilder errMessBuilder = new StringBuilder("Validation errors:\n");
        List<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations().stream().toList();
        IntStream.range(0, constraintViolations.size())
                .forEach(i -> errMessBuilder.append(i + 1)
                        .append(". ")
                        .append(constraintViolations.get(i).getMessage())
                        .append("\n")
                );
        ResponseEntity<String> body = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errMessBuilder.toString());
        logger.debug("In inputParam(s) were found {} invalidValue, response body = {}", constraintViolations.size(), body.getBody());
        return body;
    }
}
