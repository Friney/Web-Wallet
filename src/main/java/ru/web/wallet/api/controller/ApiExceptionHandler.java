package ru.web.wallet.api.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.web.wallet.core.exception.AppError;
import ru.web.wallet.core.exception.ServiceException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

@Slf4j
@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<AppError> handle(ServiceException exception) {
        return new ResponseEntity<>(exception.getAppError(), HttpStatus.valueOf(exception.getAppError().statusCode()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<AppError>> handle(MethodArgumentNotValidException ex) {
        List<AppError> errors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(
                        error -> new AppError(HttpStatus.BAD_REQUEST.value(), ((FieldError) error).getField() + " " + error.getDefaultMessage())
                )
                .toList();
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<AppError> handle(HttpMessageNotReadableException exception) {
        return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), exception.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<AppError> handle(RuntimeException exception) {
        log.info(exception.getClass() + " -> " + exception.getMessage());
        return new ResponseEntity<>(new AppError(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
