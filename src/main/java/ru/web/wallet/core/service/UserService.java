package ru.web.wallet.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.web.wallet.api.dto.UserCreateRequest;
import ru.web.wallet.api.dto.UserDto;
import ru.web.wallet.api.dto.UserPatchRequest;
import ru.web.wallet.core.entity.User;
import ru.web.wallet.core.exception.AppError;
import ru.web.wallet.core.exception.ServiceException;
import ru.web.wallet.core.mapper.UserMapper;
import ru.web.wallet.core.repository.UserRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final WalletService walletService;

    public UserDto create(UserCreateRequest userCreateRequest) {
        User user = userMapper.map(userCreateRequest);
        user = userRepository.save(user);
        walletService.create(user);
        return userMapper.map(user);
    }

    public List<UserDto> getAll() {
        return userMapper.map(userRepository.findAll());
    }

    public UserDto getById(UUID id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new ServiceException(new AppError(HttpStatus.NOT_FOUND.value(), String.format("User with id %s not found", id)))
        );
        return userMapper.map(user);
    }

    public UserDto update(UUID id, UserPatchRequest userPatchRequest) {
        User user = userMapper.map(userRepository.findById(id).orElseThrow(
                () -> new ServiceException(new AppError(HttpStatus.NOT_FOUND.value(), String.format("User with id %s not found", id)))
        ), userPatchRequest);
        userRepository.save(user);
        return userMapper.map(user);
    }
}
