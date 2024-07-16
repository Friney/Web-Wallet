package ru.web.wallet.api.controller;

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import ru.web.wallet.api.dto.UserCreateRequest;
import ru.web.wallet.api.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.web.wallet.api.Paths;
import ru.web.wallet.api.dto.UserPatchRequest;
import ru.web.wallet.core.service.UserService;

import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping(Paths.USERS)
    public List<UserDto> get() {
        return userService.getAll();
    }

    @GetMapping(Paths.USER_ID)
    public UserDto getById(@Valid @PathVariable UUID id) {
        return userService.getById(id);
    }

    @PostMapping(Paths.USERS)
    public UserDto create(@Valid @RequestBody UserCreateRequest userCreateRequest) {
        return userService.create(userCreateRequest);
    }

    @PatchMapping(Paths.USER_ID)
    public UserDto update(@Valid @PathVariable UUID id, @Valid @RequestBody UserPatchRequest userCreateRequest) {
        return userService.update(id, userCreateRequest);
    }
}
