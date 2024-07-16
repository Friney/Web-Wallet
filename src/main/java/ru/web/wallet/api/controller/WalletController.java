package ru.web.wallet.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import ru.web.wallet.api.Paths;
import ru.web.wallet.api.dto.WalletDto;
import ru.web.wallet.core.service.WalletService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class WalletController {
    private final WalletService walletService;

    @GetMapping(Paths.WALLET_USER_ID)
    public WalletDto get(@PathVariable UUID id) {
        return walletService.getUserWallet(id);
    }
}
