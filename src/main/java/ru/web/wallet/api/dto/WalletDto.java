package ru.web.wallet.api.dto;

import lombok.Builder;

@Builder
public record WalletDto(
        Long number,
        Integer balance
) {
}
