package com.evgeniyfedorchenko.expAssistant.exceptions.handler;

import com.evgeniyfedorchenko.expAssistant.exceptions.InvalidControllerParameterException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
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
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errMessBuilder.toString());
    }

    @ExceptionHandler(InvalidControllerParameterException.class)    // Невалидные параметры inputDto
    public ResponseEntity<String> handleInvalidControllerParameterException(InvalidControllerParameterException e) {
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
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errMessBuilder.toString());
    }
}
