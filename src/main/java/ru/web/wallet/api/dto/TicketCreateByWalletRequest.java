package ru.web.wallet.api.dto;

import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.NonNull;

@Builder
public record TicketCreateByWalletRequest(
        @NonNull
        Long numberWallet,
        @NonNull
        @Min(1)
        Integer amount
) {
}
