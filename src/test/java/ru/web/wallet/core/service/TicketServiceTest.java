package ru.web.wallet.core.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.ConversionService;
import ru.web.wallet.api.TicketStatus;
import ru.web.wallet.api.TicketType;
import ru.web.wallet.api.dto.TicketCreateByPhoneRequest;
import ru.web.wallet.api.dto.TicketCreateByWalletRequest;
import ru.web.wallet.api.dto.TicketDto;
import ru.web.wallet.api.dto.TicketFindByFilteredRequest;
import ru.web.wallet.core.entity.Ticket;
import ru.web.wallet.core.entity.User;
import ru.web.wallet.core.entity.Wallet;
import ru.web.wallet.core.exception.ServiceException;
import ru.web.wallet.core.mapper.TicketMapper;
import ru.web.wallet.core.repository.TicketRepository;
import ru.web.wallet.core.repository.UserRepository;
import ru.web.wallet.core.repository.WalletRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TicketServiceTest {
    @Mock
    private TicketMapper ticketMapper;
    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private WalletService walletService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private WalletRepository walletRepository;
    @Mock
    private ConversionService conversionService;
    @InjectMocks
    private TicketService ticketService;

    private User senderUser;
    private Wallet senderWallet;
    private User receiverUser;
    private Wallet receiverWallet;
    private Ticket ticketPaidIncome;
    private Ticket ticketPaidOutcome;
    private Ticket ticketNotPaidIncome;
    private Ticket ticketNotPaidOutcome;
    private TicketDto ticketPaidIncomeDto;
    private TicketDto ticketPaidOutcomeDto;
    private TicketDto ticketNotPaidIncomeDto;
    private TicketDto ticketNotPaidOutcomeDto;

    private TicketCreateByPhoneRequest ticketCreateByPhoneRequest;
    private TicketCreateByWalletRequest ticketCreateByWalletRequest;

    @BeforeEach
    void setUp() {
        senderUser = new User(UUID.randomUUID(), "testSender", "testSender", "testSender", "testSender",
                "testSender", LocalDate.now(), "testSender");
        senderWallet = new Wallet(1L, 100, senderUser);

        receiverUser = new User(UUID.randomUUID(), "testReceiver", "testReceiver", "testReceiver", "testReceiver",
                "testReceiver", LocalDate.now(), "test");
        receiverWallet = new Wallet(2L, 100, receiverUser);

        ticketPaidIncomeDto = new TicketDto(UUID.randomUUID(), 100, LocalDateTime.now());
        ticketPaidOutcomeDto = new TicketDto(UUID.randomUUID(), 200, LocalDateTime.now());
        ticketNotPaidIncomeDto = new TicketDto(UUID.randomUUID(), 300, LocalDateTime.now());
        ticketNotPaidOutcomeDto = new TicketDto(UUID.randomUUID(), 400, LocalDateTime.now());

        ticketPaidIncome = new Ticket(ticketPaidIncomeDto.id(), ticketPaidIncomeDto.creationDate(), ticketPaidIncomeDto.amount(), TicketStatus.PAID, receiverUser, senderUser);
        ticketPaidOutcome = new Ticket(ticketPaidOutcomeDto.id(), ticketPaidOutcomeDto.creationDate(), ticketPaidOutcomeDto.amount(), TicketStatus.PAID, senderUser, receiverUser);
        ticketNotPaidIncome = new Ticket(ticketNotPaidIncomeDto.id(), ticketNotPaidIncomeDto.creationDate(), ticketNotPaidIncomeDto.amount(), TicketStatus.NOT_PAID, receiverUser, senderUser);
        ticketNotPaidOutcome = new Ticket(ticketNotPaidOutcomeDto.id(), ticketNotPaidOutcomeDto.creationDate(), ticketNotPaidOutcomeDto.amount(), TicketStatus.NOT_PAID, senderUser, receiverUser);

        ticketCreateByPhoneRequest = new TicketCreateByPhoneRequest("testReceiver", 100);
        ticketCreateByWalletRequest = new TicketCreateByWalletRequest(1L, 100);
    }

    void setupAllTicketsMapper() {
        when(ticketRepository.findAll()).thenReturn(List.of(ticketPaidIncome, ticketPaidOutcome, ticketNotPaidIncome, ticketNotPaidOutcome));
        when(ticketMapper.map(ArgumentMatchers.anyList())).thenAnswer(input -> input
                .<List<Ticket>>getArgument(0)
                .stream()
                .map(ticket -> {
                    if (ticket.getPaid() == ticketPaidOutcome.getPaid()) {
                        if (ticket.getSenderUser().equals(senderUser)) {
                            return ticketPaidOutcomeDto;
                        }
                        return ticketPaidIncomeDto;
                    }
                    if (ticket.getSenderUser().equals(senderUser)) {
                        return ticketNotPaidOutcomeDto;
                    }
                    return ticketNotPaidIncomeDto;
                })
                .toList()
        );
    }

    @Test
    void testGetByIdFailed() {
        when(ticketRepository.findById(ticketPaidOutcome.getId())).thenReturn(Optional.empty());

        assertThrows(ServiceException.class, () -> ticketService.getById(ticketPaidOutcome.getId()));
    }

    @Test
    void testGetById() {
        when(ticketRepository.findById(ticketPaidOutcome.getId())).thenReturn(Optional.of(ticketPaidOutcome));
        when(ticketMapper.map(ticketPaidOutcome)).thenReturn(ticketPaidOutcomeDto);

        assertEquals(ticketPaidOutcomeDto, ticketService.getById(ticketPaidOutcome.getId()));
    }

    @Test
    void testCreateByPhoneFailedNoSenderWallet() {
        when(walletRepository.findByUserId(senderUser.getId())).thenReturn(Optional.empty());

        assertThrows(ServiceException.class, () -> ticketService.createByPhone(ticketCreateByPhoneRequest, senderUser.getId()));
    }

    @Test
    void testCreateByPhoneFailedNoReceiverUser() {
        when(walletRepository.findByUserId(senderUser.getId())).thenReturn(Optional.of(senderWallet));
        when(userRepository.findByPhone(ticketCreateByPhoneRequest.phone())).thenReturn(Optional.empty());

        assertThrows(ServiceException.class, () -> ticketService.createByPhone(ticketCreateByPhoneRequest, senderUser.getId()));
    }

    @Test
    void testCreateByPhoneFailedNoReceiverWallet() {
        when(walletRepository.findByUserId(senderUser.getId())).thenReturn(Optional.of(senderWallet));
        when(userRepository.findByPhone(ticketCreateByPhoneRequest.phone())).thenReturn(Optional.of(receiverUser));
        when(walletRepository.findByUserId(receiverUser.getId())).thenReturn(Optional.empty());

        assertThrows(ServiceException.class, () -> ticketService.createByPhone(ticketCreateByPhoneRequest, senderUser.getId()));
    }

    @Test
    void testCreateByPhoneFailedIdenticalWallets() {
        when(walletRepository.findByUserId(senderUser.getId())).thenReturn(Optional.of(senderWallet));
        when(userRepository.findByPhone(ticketCreateByPhoneRequest.phone())).thenReturn(Optional.of(senderUser));

        assertThrows(ServiceException.class, () -> ticketService.createByPhone(ticketCreateByPhoneRequest, senderUser.getId()));
    }

    @Test
    void testCreateByPhonePaid() {
        when(walletService.getBalance(senderUser.getId())).thenReturn(100);
        when(walletRepository.findByUserId(senderUser.getId())).thenReturn(Optional.of(senderWallet));
        when(userRepository.findByPhone(ticketCreateByPhoneRequest.phone())).thenReturn(Optional.of(receiverUser));
        when(walletRepository.findByUserId(receiverUser.getId())).thenReturn(Optional.of(receiverWallet));
        when(ticketRepository.save(any(Ticket.class)))
                .thenAnswer(input -> {
                    Ticket t = input.getArgument(0);
                    if (t.getPaid() == TicketStatus.PAID &&
                            t.getAmount().equals(ticketCreateByPhoneRequest.amount()) &&
                            t.getReceiverUser().getId().equals(receiverUser.getId()) &&
                            t.getSenderUser().getId().equals(senderUser.getId())) {
                        return ticketPaidOutcome;
                    }
                    return t;
                });
        when(ticketMapper.map(ticketPaidOutcome)).thenReturn(ticketPaidOutcomeDto);

        assertEquals(ticketPaidOutcomeDto, ticketService.createByPhone(ticketCreateByPhoneRequest, senderUser.getId()));
    }

    @Test
    void testCreateByPhoneNotPaid() {
        when(walletService.getBalance(senderUser.getId())).thenReturn(0);
        when(walletRepository.findByUserId(senderUser.getId())).thenReturn(Optional.of(senderWallet));
        when(userRepository.findByPhone(ticketCreateByPhoneRequest.phone())).thenReturn(Optional.of(receiverUser));
        when(walletRepository.findByUserId(receiverUser.getId())).thenReturn(Optional.of(receiverWallet));
        when(ticketRepository.save(any(Ticket.class)))
                .thenAnswer(input -> {
                    Ticket t = input.getArgument(0);
                    if (t.getPaid() == TicketStatus.NOT_PAID &&
                            t.getAmount().equals(ticketCreateByPhoneRequest.amount()) &&
                            t.getReceiverUser().getId().equals(receiverUser.getId()) &&
                            t.getSenderUser().getId().equals(senderUser.getId())) {
                        return ticketPaidOutcome;
                    }
                    return t;
                });
        when(ticketMapper.map(ticketPaidOutcome)).thenReturn(ticketPaidOutcomeDto);

        assertEquals(ticketPaidOutcomeDto, ticketService.createByPhone(ticketCreateByPhoneRequest, senderUser.getId()));
    }

    @Test
    void testCreateByWalletFailedNoSenderWallet() {
        when(walletRepository.findByUserId(senderUser.getId())).thenReturn(Optional.empty());

        assertThrows(ServiceException.class, () -> ticketService.createByWallet(ticketCreateByWalletRequest, senderUser.getId()));
    }

    @Test
    void testCreateByWalletFailedNoReceiverWallet() {
        when(walletRepository.findByUserId(senderUser.getId())).thenReturn(Optional.of(senderWallet));
        when(walletRepository.findById(ticketCreateByWalletRequest.numberWallet())).thenReturn(Optional.empty());

        assertThrows(ServiceException.class, () -> ticketService.createByWallet(ticketCreateByWalletRequest, senderUser.getId()));
    }

    @Test
    void testCreateByWalletFailedIdenticalWallets() {
        when(walletRepository.findByUserId(senderUser.getId())).thenReturn(Optional.of(senderWallet));
        when(walletRepository.findById(ticketCreateByWalletRequest.numberWallet())).thenReturn(Optional.of(senderWallet));

        assertThrows(ServiceException.class, () -> ticketService.createByWallet(ticketCreateByWalletRequest, senderUser.getId()));
    }

    @Test
    void testCreateByWalletPaid() {
        when(walletService.getBalance(senderUser.getId())).thenReturn(100);
        when(walletRepository.findByUserId(senderUser.getId())).thenReturn(Optional.of(senderWallet));
        when(walletRepository.findById(ticketCreateByWalletRequest.numberWallet())).thenReturn(Optional.of(receiverWallet));
        when(ticketRepository.save(any(Ticket.class)))
                .thenAnswer(input -> {
                    Ticket t = input.getArgument(0);
                    if (t.getPaid() == TicketStatus.PAID &&
                            t.getAmount().equals(ticketCreateByWalletRequest.amount()) &&
                            t.getReceiverUser().getId().equals(receiverWallet.getUser().getId()) &&
                            t.getSenderUser().getId().equals(senderUser.getId())) {
                        return ticketPaidOutcome;
                    }
                    return t;
                });
        when(ticketMapper.map(ticketPaidOutcome)).thenReturn(ticketPaidOutcomeDto);

        assertEquals(ticketPaidOutcomeDto, ticketService.createByWallet(ticketCreateByWalletRequest, senderUser.getId()));
    }

    @Test
    void testCreateByWalletNotPaid() {
        when(walletService.getBalance(senderUser.getId())).thenReturn(0);
        when(walletRepository.findByUserId(senderUser.getId())).thenReturn(Optional.of(senderWallet));
        when(walletRepository.findById(ticketCreateByWalletRequest.numberWallet())).thenReturn(Optional.of(receiverWallet));
        when(ticketRepository.save(any(Ticket.class)))
                .thenAnswer(input -> {
                    Ticket t = input.getArgument(0);
                    if (t.getPaid() == TicketStatus.NOT_PAID &&
                            t.getAmount().equals(ticketCreateByWalletRequest.amount()) &&
                            t.getReceiverUser().getId().equals(receiverWallet.getUser().getId()) &&
                            t.getSenderUser().getId().equals(senderUser.getId())) {
                        return ticketPaidOutcome;
                    }
                    return t;
                });
        when(ticketMapper.map(ticketPaidOutcome)).thenReturn(ticketPaidOutcomeDto);

        assertEquals(ticketPaidOutcomeDto, ticketService.createByWallet(ticketCreateByWalletRequest, senderUser.getId()));
    }

    @Test
    void testGetAllFilteredNotTicketsForUser() {
        TicketFindByFilteredRequest ticketFindByFilteredRequest = new TicketFindByFilteredRequest(null, null, null);
        setupAllTicketsMapper();

        assertEquals(List.of(), ticketService.getAllFiltered(ticketFindByFilteredRequest, UUID.randomUUID()));
    }

    @Test
    void testGetAllFilteredPaid() {
        TicketFindByFilteredRequest ticketFindByFilteredRequest = new TicketFindByFilteredRequest(TicketStatus.PAID.toString(), null, null);
        when(conversionService.convert(ticketFindByFilteredRequest.type(), TicketType.class)).thenReturn(null);
        when(conversionService.convert(ticketFindByFilteredRequest.status(), TicketStatus.class)).thenReturn(TicketStatus.valueOf(ticketFindByFilteredRequest.status()));
        setupAllTicketsMapper();

        assertEquals(List.of(ticketPaidIncomeDto, ticketPaidOutcomeDto), ticketService.getAllFiltered(ticketFindByFilteredRequest, senderUser.getId()));
    }

    @Test
    void testGetAllFilteredNotPaid() {
        TicketFindByFilteredRequest ticketFindByFilteredRequest = new TicketFindByFilteredRequest(TicketStatus.NOT_PAID.toString(), null, null);
        when(conversionService.convert(ticketFindByFilteredRequest.type(), TicketType.class)).thenReturn(null);
        when(conversionService.convert(ticketFindByFilteredRequest.status(), TicketStatus.class)).thenReturn(TicketStatus.valueOf(ticketFindByFilteredRequest.status()));
        setupAllTicketsMapper();

        assertEquals(List.of(ticketNotPaidIncomeDto, ticketNotPaidOutcomeDto), ticketService.getAllFiltered(ticketFindByFilteredRequest, senderUser.getId()));
    }

    @Test
    void testGetAllFilteredIncome() {
        TicketFindByFilteredRequest ticketFindByFilteredRequest = new TicketFindByFilteredRequest(null, TicketType.INCOME.toString(), null);
        when(conversionService.convert(ticketFindByFilteredRequest.type(), TicketType.class)).thenReturn(TicketType.valueOf(ticketFindByFilteredRequest.type()));
        setupAllTicketsMapper();

        assertEquals(List.of(ticketPaidIncomeDto, ticketNotPaidIncomeDto), ticketService.getAllFiltered(ticketFindByFilteredRequest, senderUser.getId()));
    }

    @Test
    void testGetAllFilteredOutcome() {
        TicketFindByFilteredRequest ticketFindByFilteredRequest = new TicketFindByFilteredRequest(null, TicketType.OUTCOME.toString(), null);
        when(conversionService.convert(ticketFindByFilteredRequest.type(), TicketType.class)).thenReturn(TicketType.valueOf(ticketFindByFilteredRequest.type()));
        setupAllTicketsMapper();

        assertEquals(List.of(ticketPaidOutcomeDto, ticketNotPaidOutcomeDto), ticketService.getAllFiltered(ticketFindByFilteredRequest, senderUser.getId()));
    }

    @Test
    void testGetAllFilteredWithUser() {
        TicketFindByFilteredRequest ticketFindByFilteredRequest = new TicketFindByFilteredRequest(null, null, senderUser.getId());
        setupAllTicketsMapper();

        assertEquals(List.of(ticketPaidIncomeDto, ticketPaidOutcomeDto, ticketNotPaidIncomeDto, ticketNotPaidOutcomeDto), ticketService.getAllFiltered(ticketFindByFilteredRequest, senderUser.getId()));

        ticketFindByFilteredRequest = new TicketFindByFilteredRequest(null, null, UUID.randomUUID());

        assertEquals(List.of(), ticketService.getAllFiltered(ticketFindByFilteredRequest, senderUser.getId()));
    }
}