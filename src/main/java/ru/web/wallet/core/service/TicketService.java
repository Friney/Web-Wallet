package ru.web.wallet.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import ru.web.wallet.api.TicketStatus;
import ru.web.wallet.api.TicketType;
import ru.web.wallet.api.dto.TicketCreateByPhoneRequest;
import ru.web.wallet.api.dto.TicketCreateByWalletRequest;
import ru.web.wallet.api.dto.TicketDto;
import ru.web.wallet.api.dto.TicketFindByFilteredRequest;
import ru.web.wallet.core.entity.Ticket;
import ru.web.wallet.core.entity.User;
import ru.web.wallet.core.entity.Wallet;
import ru.web.wallet.core.exception.AppError;
import ru.web.wallet.core.exception.ServiceException;
import ru.web.wallet.core.mapper.TicketMapper;
import ru.web.wallet.core.repository.TicketRepository;
import ru.web.wallet.core.repository.UserRepository;
import ru.web.wallet.core.repository.WalletRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketService {
    private final TicketMapper ticketMapper;
    private final TicketRepository ticketRepository;
    private final WalletService walletService;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final ConversionService conversionService;

    public List<TicketDto> getAllFiltered(TicketFindByFilteredRequest ticketFindByFilteredRequest, @PathVariable UUID userId) {
        TicketType ticketType = conversionService.convert(ticketFindByFilteredRequest.type(), TicketType.class);
        TicketStatus ticketStatus = conversionService.convert(ticketFindByFilteredRequest.status(), TicketStatus.class);
        List<Ticket> ticketList = ticketRepository
                .findAll()
                .stream()
                .filter(
                        ticket -> ticket
                                .getReceiverUser()
                                .getId()
                                .equals(userId)
                                || ticket
                                .getSenderUser()
                                .getId()
                                .equals(userId)
                )
                .filter(
                        ticket -> ticketStatus == null
                                || ticketStatus.equals(ticket.getPaid())
                )
                .filter(
                        ticket -> ticketType == null
                                || ticketType.equals(TicketType.INCOME) == ticket
                                .getReceiverUser()
                                .getId()
                                .equals(userId)
                                || ticketType.equals(TicketType.OUTCOME) == ticket
                                .getSenderUser()
                                .getId()
                                .equals(userId)
                )
                .filter(
                        ticket -> ticketFindByFilteredRequest.userId() == null
                                || ticket
                                .getReceiverUser()
                                .getId()
                                .equals(ticketFindByFilteredRequest.userId())
                                || ticket.getSenderUser()
                                .getId()
                                .equals(ticketFindByFilteredRequest.userId())
                )
                .toList();
        return ticketMapper.map(ticketList);
    }

    public TicketDto getById(UUID id) {
        return ticketMapper.map(ticketRepository.findById(id).orElseThrow(
                () -> new ServiceException(new AppError(HttpStatus.NOT_FOUND.value(), String.format("Transfer with id %s not found", id)))
        ));
    }

    public TicketDto createByPhone(TicketCreateByPhoneRequest transferCreateByPhone, UUID senderId) {
        boolean paid = walletService.getBalance(senderId) >= transferCreateByPhone.amount();
        Wallet senderWallet = walletRepository.findByUserId(senderId).orElseThrow(
                () -> new ServiceException(new AppError(HttpStatus.NOT_FOUND.value(), String.format("Wallet not found for user %s", senderId)))
        );
        User receiver = userRepository.findByPhone(transferCreateByPhone.phone()).orElseThrow(
                () -> new ServiceException(new AppError(HttpStatus.NOT_FOUND.value(), String.format("User with phone %s not found", transferCreateByPhone.phone())))
        );
        Wallet receiverWallet = walletRepository.findByUserId(receiver.getId()).orElseThrow(
                () -> new ServiceException(new AppError(HttpStatus.NOT_FOUND.value(), String.format("Wallet not found for user %s", receiver.getId())))
        );
        return getTicketDto(paid, senderWallet, receiverWallet, transferCreateByPhone.amount());
    }

    public TicketDto createByWallet(TicketCreateByWalletRequest ticketCreateByWalletRequest, UUID senderId) {
        boolean paid = walletService.getBalance(senderId) >= ticketCreateByWalletRequest.amount();
        Wallet senderWallet = walletRepository.findByUserId(senderId).orElseThrow(
                () -> new ServiceException(new AppError(HttpStatus.NOT_FOUND.value(), String.format("Wallet not found for user %s", senderId)))
        );
        Wallet receiverWallet = walletRepository.findById(ticketCreateByWalletRequest.numberWallet()).orElseThrow(
                () -> new ServiceException(new AppError(HttpStatus.NOT_FOUND.value(), String.format("Wallet not found for number %s", ticketCreateByWalletRequest.numberWallet())))
        );
        return getTicketDto(paid, senderWallet, receiverWallet, ticketCreateByWalletRequest.amount());
    }

    private TicketDto getTicketDto(boolean paid, Wallet senderWallet, Wallet receiverWallet, int amount) {
        if (senderWallet.equals(receiverWallet)) {
            throw new ServiceException(new AppError(HttpStatus.BAD_REQUEST.value(), "You can't transfer money to yourself"));
        }
        if (paid) {
            senderWallet.setBalance(senderWallet.getBalance() - amount);
            receiverWallet.setBalance(receiverWallet.getBalance() + amount);
        }
        TicketStatus paidStatus = paid ? TicketStatus.PAID : TicketStatus.NOT_PAID;
        return ticketMapper.map(createTransfer(senderWallet, receiverWallet, amount, paidStatus));
    }

    private Ticket createTransfer(Wallet senderWallet, Wallet recipientWallet, Integer amount, TicketStatus paid) {
        Ticket ticket = new Ticket();
        ticket.setCreationDate(LocalDateTime.now());
        ticket.setAmount(amount);
        ticket.setPaid(paid);
        ticket.setSenderUser(senderWallet.getUser());
        ticket.setReceiverUser(recipientWallet.getUser());
        return ticketRepository.save(ticket);
    }

}
