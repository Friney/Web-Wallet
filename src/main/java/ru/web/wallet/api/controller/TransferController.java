package ru.web.wallet.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.web.wallet.api.Paths;
import ru.web.wallet.api.dto.TicketCreateByPhoneRequest;
import ru.web.wallet.api.dto.TicketCreateByWalletRequest;
import ru.web.wallet.api.dto.TicketDto;
import ru.web.wallet.api.dto.TicketFindByFilteredRequest;
import ru.web.wallet.core.service.TicketService;

import java.util.List;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
public class TransferController {
    private final TicketService ticketService;

    @GetMapping(Paths.TRANSFERS)
    public List<TicketDto> getAllFiltered(@RequestBody TicketFindByFilteredRequest ticketFindByFilteredRequest, @RequestParam UUID userId) {
        return ticketService.getAllFiltered(ticketFindByFilteredRequest, userId);
    }

    @GetMapping(Paths.TRANSFERS_ID)
    public TicketDto getById(@PathVariable UUID id) {
        return ticketService.getById(id);
    }

    @PostMapping(Paths.TRANSFERS_PHONE)
    public TicketDto createByPhone(@RequestBody TicketCreateByPhoneRequest transferCreatePhone, @PathVariable UUID senderId) {
        return ticketService.createByPhone(transferCreatePhone, senderId);
    }

    @PostMapping(Paths.TRANSFERS_WALLET)
    public TicketDto createByWallet(@RequestBody TicketCreateByWalletRequest ticketCreateByWalletRequest, @PathVariable UUID senderId) {
        return ticketService.createByWallet(ticketCreateByWalletRequest, senderId);
    }
}
