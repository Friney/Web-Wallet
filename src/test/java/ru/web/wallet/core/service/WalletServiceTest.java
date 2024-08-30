package ru.web.wallet.core.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.web.wallet.api.dto.WalletDto;
import ru.web.wallet.core.entity.User;
import ru.web.wallet.core.entity.Wallet;
import ru.web.wallet.core.exception.ServiceException;
import ru.web.wallet.core.mapper.WalletMapper;
import ru.web.wallet.core.repository.WalletRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {
    @Mock
    private WalletMapper walletMapper;
    @Mock
    private WalletRepository walletRepository;
    @InjectMocks

    private WalletService walletService;
    private User user;
    private Wallet wallet;
    private WalletDto walletDto;

    @BeforeEach
    void setUp() {
        user = new User(UUID.randomUUID(), "test", "test", "test", "test",
                "test", LocalDate.now(), "test");
        wallet = new Wallet(1L, 100, user);
        walletDto = new WalletDto(wallet.getNumber(), wallet.getBalance());
    }

    @Test
    void create() {
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);
        when(walletMapper.map(wallet)).thenReturn(walletDto);

        assertEquals(walletMapper.map(wallet), walletService.create(user));
    }

    @Test
    void getUserWalletFailed() {
        when(walletRepository.findByUserId(user.getId())).thenReturn(Optional.empty());

        assertThrows(ServiceException.class, () -> walletService.getUserWallet(user.getId()));
    }

    @Test
    void getUserWallet() {
        when(walletRepository.findByUserId(user.getId())).thenReturn(Optional.of(wallet));
        when(walletMapper.map(wallet)).thenReturn(walletDto);

        assertEquals(walletDto, walletService.getUserWallet(user.getId()));
    }

    @Test
    void getBalanceFailed() {
        when(walletRepository.findByUserId(user.getId())).thenReturn(Optional.empty());

        assertThrows(ServiceException.class, () -> walletService.getBalance(user.getId()));
    }

    @Test
    void getBalance() {
        when(walletRepository.findByUserId(user.getId())).thenReturn(Optional.of(wallet));

        assertEquals(wallet.getBalance(), walletService.getBalance(user.getId()));
    }
}