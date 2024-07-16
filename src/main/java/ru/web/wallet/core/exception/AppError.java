package ru.web.wallet.core.exception;

public record AppError(
        int statusCode,
        String message) {
}
