package ru.web.wallet.api.dto;

import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder
public record UserDto(
        UUID id,
        String name,
        String surname,
        String patronymic,
        String phone,
        String email,
        LocalDate birthDate) {
}
