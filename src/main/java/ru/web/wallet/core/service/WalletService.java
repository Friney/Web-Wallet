package ru.web.wallet.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.web.wallet.api.dto.WalletDto;
import ru.web.wallet.core.entity.User;
import ru.web.wallet.core.entity.Wallet;
import ru.web.wallet.core.exception.AppError;
import ru.web.wallet.core.exception.ServiceException;
import ru.web.wallet.core.mapper.WalletMapper;
import ru.web.wallet.core.repository.WalletRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletService {
    private final WalletRepository walletRepository;
    private final WalletMapper walletMapper;

    public void create(User user) {
        Wallet wallet = new Wallet();
        wallet.setBalance(100);
        wallet.setUser(user);
        walletRepository.save(wallet);
    }

    public WalletDto getUserWallet(UUID id) {
        Wallet wallet = walletRepository.findByUserId(id).orElseThrow(
                () -> new ServiceException(new AppError(HttpStatus.NOT_FOUND.value(), String.format("Wallet not found for user %s", id)))
        );
        return walletMapper.map(wallet);
    }

    public Integer getBalance(UUID id) {
        return walletRepository.findByUserId(id).orElseThrow(
                () -> new ServiceException(new AppError(HttpStatus.NOT_FOUND.value(), String.format("Wallet not found for user %s", id)))
        ).getBalance();
    }
}
