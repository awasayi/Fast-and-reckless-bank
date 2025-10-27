package revel8.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import revel8.dto.*;
import revel8.exception.AccountNotFoundException;
import revel8.exception.InsufficientFundsException;
import revel8.exception.InvalidAmountException;
import revel8.exception.InvalidTransferException;
import revel8.model.Account;
import revel8.model.TransferRecord;
import revel8.repository.InMemoryAccountRepository;
import revel8.service.AccountService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static revel8.mapper.AccountMapper.ACCOUNT_MAPPER;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private static final String ACCOUNT_NOT_FOUND_MESSAGE = "Account not found: ";
    
    private final InMemoryAccountRepository accountRepository;

    @Override
    public CreateAccountResponse createAccount(CreateAccountRequest request) {
        long cents = parseAmount(request.initialDeposit());
        if (cents < 0) {
            throw new InvalidAmountException("Initial deposit cannot be negative");
        }
        
        Account account = accountRepository.create(request.name(), request.email(), request.age(), request.city(), cents);
        return ACCOUNT_MAPPER.toCreateAccountResponse(account);
    }

    @Override
    public BalanceResponse deposit(UUID accountId, AmountRequest request) {
        long cents = parseAmount(request.amount());
        if (cents <= 0) {
            throw new InvalidAmountException("Deposit amount must be positive");
        }
        
        Account acc = accountRepository.findById(accountId)
            .orElseThrow(() -> new AccountNotFoundException(ACCOUNT_NOT_FOUND_MESSAGE + accountId));
        
        acc.getLock().lock();
        try {
            long newBal = acc.getBalanceInCents() + cents;
            acc.setBalanceInCents(newBal);
            return ACCOUNT_MAPPER.toBalanceResponse(ACCOUNT_MAPPER.formatAmount(newBal));
        } finally {
            acc.getLock().unlock();
        }
    }

    @Override
    public BalanceResponse withdraw(UUID accountId, AmountRequest request) {
        long cents = parseAmount(request.amount());
        if (cents <= 0) {
            throw new InvalidAmountException("Withdrawal amount must be positive");
        }
        
        Account acc = accountRepository.findById(accountId)
            .orElseThrow(() -> new AccountNotFoundException(ACCOUNT_NOT_FOUND_MESSAGE + accountId));
        
        acc.getLock().lock();
        try {
            long curr = acc.getBalanceInCents();
            if (curr < cents) {
                throw new InsufficientFundsException("Insufficient funds for withdrawal");
            }
            
            long newBal = curr - cents;
            acc.setBalanceInCents(newBal);
            
            return ACCOUNT_MAPPER.toBalanceResponse(ACCOUNT_MAPPER.formatAmount(newBal));
        } finally {
            acc.getLock().unlock();
        }
    }

    @Override
    public TransferResponse transfer(TransferRequest request) {
        long cents = parseAmount(request.amount());
        if (cents <= 0) {
            throw new InvalidAmountException("Transfer amount must be positive");
        }
        if (request.fromAccountId().equals(request.toAccountId())) {
            throw new InvalidTransferException("Cannot transfer to the same account");
        }

        Account a1 = accountRepository.findById(request.fromAccountId())
            .orElseThrow(() -> new AccountNotFoundException("Source " + ACCOUNT_NOT_FOUND_MESSAGE.toLowerCase() + request.fromAccountId()));
        Account a2 = accountRepository.findById(request.toAccountId())
            .orElseThrow(() -> new AccountNotFoundException("Destination " + ACCOUNT_NOT_FOUND_MESSAGE.toLowerCase() + request.toAccountId()));

        Account first = (request.fromAccountId().compareTo(request.toAccountId()) < 0) ? a1 : a2;
        Account second = (first == a1) ? a2 : a1;

        first.getLock().lock();
        second.getLock().lock();
        try {
            long srcBalance = a1.getBalanceInCents();
            if (srcBalance < cents) {
                throw new InsufficientFundsException("Insufficient funds for transfer");
            }

            a1.setBalanceInCents(srcBalance - cents);
            a2.setBalanceInCents(a2.getBalanceInCents() + cents);

            long senderNewBalance = a1.getBalanceInCents();
            long recipientNewBalance = a2.getBalanceInCents();
            
            TransferRecord transferRecord = new TransferRecord(
                UUID.randomUUID(),
                request.toAccountId(),
                cents,
                Instant.now().toEpochMilli(),
                senderNewBalance
            );
            a1.getOutgoing().append(transferRecord);

            return new TransferResponse(
                transferRecord.transferId(),
                transferRecord.toAccountId(),
                ACCOUNT_MAPPER.formatAmount(cents),
                transferRecord.timestampMillis(),
                ACCOUNT_MAPPER.formatAmount(senderNewBalance),
                ACCOUNT_MAPPER.formatAmount(recipientNewBalance)
            );
        } finally {
            second.getLock().unlock();
            first.getLock().unlock();
        }
    }

    @Override
    public OutgoingTransfersResponse getOutgoingTransfers(UUID accountId) {
        Account acc = accountRepository.findById(accountId)
            .orElseThrow(() -> new AccountNotFoundException(ACCOUNT_NOT_FOUND_MESSAGE + accountId));
        
        acc.getLock().lock();
        try {
            var transfers = acc.getOutgoing().getRecentNewestFirst().stream()
                .map(tr -> new TransferResponse(
                    tr.transferId(),
                    tr.toAccountId(),
                    ACCOUNT_MAPPER.formatAmount(tr.amountInCents()),
                    tr.timestampMillis(),
                    ACCOUNT_MAPPER.formatAmount(tr.resultingBalanceInCents()),
                    null  // Historical transfers don't store recipient balance
                ))
                .toList();
            return new OutgoingTransfersResponse(transfers);
        } finally {
            acc.getLock().unlock();
        }
    }

    @Override
    public List<CreateAccountResponse> getAllAccounts() {
        return accountRepository.findAll().stream()
                .map(ACCOUNT_MAPPER::toCreateAccountResponse)
                .toList();
    }

    private static long parseAmount(String amount) {
        try {
            return new BigDecimal(amount)
                .setScale(2, RoundingMode.HALF_EVEN)
                .movePointRight(2)
                .longValueExact();
        } catch (ArithmeticException | NumberFormatException e) {
            throw new InvalidAmountException("Invalid amount format");
        }
    }
}
