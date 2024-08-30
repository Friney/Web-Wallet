package ru.web.wallet.core.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.web.wallet.api.dto.UserCreateRequest;
import ru.web.wallet.api.dto.UserDto;
import ru.web.wallet.api.dto.UserPatchRequest;
import ru.web.wallet.core.entity.User;
import ru.web.wallet.core.exception.ServiceException;
import ru.web.wallet.core.mapper.UserMapper;
import ru.web.wallet.core.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserMapper userMapper;
    @Mock
    private WalletService walletService;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;

    private UserDto userDto;
    private UserCreateRequest userCreateRequest;
    private User user;
    private UserPatchRequest userPatchRequest;
    private User updatedUser;
    private UserDto updatedUserDto;

    @BeforeEach
    void setUp() {
        userDto = UserDto.builder()
                .id(UUID.randomUUID())
                .name("Иван")
                .surname("Иванов")
                .phone("71234567890")
                .email("a@b.c")
                .birthDate(LocalDate.now())
                .build();

        userCreateRequest = UserCreateRequest.builder()
                .name(userDto.name())
                .surname(userDto.surname())
                .phone(userDto.phone())
                .email(userDto.email())
                .birthDate(userDto.birthDate())
                .password("123456")
                .build();

        user = new User(userDto.id(), userDto.name(), userDto.surname(), null,
                userDto.phone(), userDto.email(), userDto.birthDate(), userCreateRequest.password());

        userPatchRequest = UserPatchRequest.builder()
                .name("Новое имя")
                .surname("Новая фамилия")
                .patronymic("Новое отчество")
                .birthDate(LocalDate.of(2000, 1, 1))
                .build();

        updatedUser = new User(userDto.id(), userPatchRequest.name(), userPatchRequest.surname(), userPatchRequest.patronymic(),
                userDto.phone(), userDto.email(), userPatchRequest.birthDate(), userCreateRequest.password());

        updatedUserDto = UserDto.builder()
                .id(updatedUser.getId())
                .name(updatedUser.getName())
                .surname(updatedUser.getSurname())
                .phone(updatedUser.getPhone())
                .email(updatedUser.getEmail())
                .birthDate(updatedUser.getBirthDate())
                .build();
    }

    @Test
    void create() {

        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.map(userCreateRequest)).thenReturn(user);
        when(userMapper.map(user)).thenReturn(userDto);

        assertEquals(userDto, userService.create(userCreateRequest));
        verify(walletService).create(user);
    }

    @Test
    void getAllEmpty() {
        when(userRepository.findAll()).thenReturn(List.of());

        assertEquals(0, userService.getAll().size());
    }

    @Test
    void getAll() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.map(List.of(user))).thenReturn(List.of(userDto));

        assertEquals(1, userService.getAll().size());
        assertEquals(userDto, userService.getAll().get(0));
    }

    @Test
    void getByIdFailed() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(ServiceException.class, () -> userService.getById(user.getId()));
    }

    @Test
    void getById() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userMapper.map(user)).thenReturn(userDto);

        assertEquals(userDto, userService.getById(user.getId()));
    }

    @Test
    void updateFailed() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(ServiceException.class, () -> userService.update(user.getId(), userPatchRequest));
    }

    @Test
    void update() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userMapper.map(user, userPatchRequest)).thenReturn(updatedUser);
        when(userMapper.map(updatedUser)).thenReturn(updatedUserDto);

        assertEquals(updatedUserDto, userService.update(user.getId(), userPatchRequest));
    }
}