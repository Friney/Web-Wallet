package ru.web.wallet.api.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Builder
public record UserPatchRequest(
        @Pattern(regexp = "^[А-Я][а-я]{0,49}$")
        String name,
        @Pattern(regexp = "^[А-Я][а-я]{0,49}$")
        String surname,
        @Pattern(regexp = "^[А-Я][а-я]{0,49}$")
        String patronymic,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate birthDate) {
}
