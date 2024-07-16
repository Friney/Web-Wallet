package ru.web.wallet.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.NonNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Builder
public record UserCreateRequest(
        @NonNull
        @Pattern(regexp = "^[А-Я][а-я]{0,49}$")
        String name,
        @NonNull
        @Pattern(regexp = "^[А-Я][а-я]{0,49}$")
        String surname,
        @Pattern(regexp = "^[А-Я][а-я]{0,49}$")
        String patronymic,
        @NonNull
        @Pattern(regexp = "^7(\\d{10})$")
        String phone,
        @NonNull
        @Email
        String email,
        @NonNull
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate birthDate,
        @NonNull
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!?])[A-Za-z\\d!?]{8,64}$")
        String password) {
}
