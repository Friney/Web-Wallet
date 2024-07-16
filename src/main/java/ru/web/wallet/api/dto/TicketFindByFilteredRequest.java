package ru.web.wallet.api.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record TicketFindByFilteredRequest(
        String status,
        String type,
        UUID userId
) {

}
