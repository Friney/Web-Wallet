package ru.web.wallet.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.NonNull;

@Builder
public record TicketCreateByPhoneRequest(
        @NonNull
        @Pattern(regexp = "^7(\\d{10})$")
        String phone,
        @NonNull
        @Min(1)
        Integer amount) {
}
