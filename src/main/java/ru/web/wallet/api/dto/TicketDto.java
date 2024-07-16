package ru.web.wallet.api.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record TicketDto(
        UUID id,
        Integer amount,
        LocalDateTime creationDate) {
}
