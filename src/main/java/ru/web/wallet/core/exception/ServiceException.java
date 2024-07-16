package ru.web.wallet.core.exception;

import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException {
    private final AppError appError;

    public ServiceException(AppError appError) {
        super();
        this.appError = appError;
    }
}
