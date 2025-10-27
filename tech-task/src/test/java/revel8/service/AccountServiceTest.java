package revel8.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import revel8.dto.*;
import revel8.exception.AccountNotFoundException;
import revel8.exception.InsufficientFundsException;
import revel8.exception.InvalidAmountException;
import revel8.exception.InvalidTransferException;
import revel8.repository.InMemoryAccountRepository;
import revel8.service.impl.AccountServiceImpl;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AccountServiceTest {
    
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        InMemoryAccountRepository repository = new InMemoryAccountRepository();
        accountService = new AccountServiceImpl(repository);
    }
    
    @Test
    void testCreateAccountSuccess() {
        var response = accountService.createAccount(
            new CreateAccountRequest("Test", "test@test.com", 25, "City", "100.00")
        );
        
        assertNotNull(response);
        assertNotNull(response.accountId());
        assertEquals("100.00", response.balance());
        assertEquals("Test", response.name());
    }
    
    @Test
    void testCreateAccountWithNegativeDeposit() {
        assertThrows(InvalidAmountException.class, () -> {
            accountService.createAccount(
                new CreateAccountRequest("Test", "test@test.com", 25, "City", "-1.00")
            );
        });
    }
    
    @Test
    void testDepositSuccess() {
        var account = accountService.createAccount(
            new CreateAccountRequest("Test", "test@test.com", 25, "City", "100.00")
        );
        
        var response = accountService.deposit(account.accountId(), new AmountRequest("50.00"));
        
        assertEquals("150.00", response.balance());
    }

    @Test
    void testDepositZeroAmount() {
        var account = accountService.createAccount(
            new CreateAccountRequest("Test", "test@test.com", 25, "City", "100.00")
        );

        assertThrows(InvalidAmountException.class, () -> {
            accountService.deposit(account.accountId(), new AmountRequest("0.00"));
        });
    }
    
    @Test
    void testDepositNegativeAmount() {
        var account = accountService.createAccount(
            new CreateAccountRequest("Test", "test@test.com", 25, "City", "100.00")
        );
        
        assertThrows(InvalidAmountException.class, () -> {
            accountService.deposit(account.accountId(), new AmountRequest("-1.00"));
        });
    }
    
    @Test
    void testDepositToNonExistentAccount() {
        UUID fakeId = UUID.randomUUID();
        
        assertThrows(AccountNotFoundException.class, () -> {
            accountService.deposit(fakeId, new AmountRequest("10.00"));
        });
    }
    
    @Test
    void testWithdrawSuccess() {
        var account = accountService.createAccount(
            new CreateAccountRequest("Test", "test@test.com", 25, "City", "100.00")
        );
        
        var response = accountService.withdraw(account.accountId(), new AmountRequest("30.00"));
        
        assertEquals("70.00", response.balance());
    }
    
    @Test
    void testWithdrawInsufficientFunds() {
        var account = accountService.createAccount(
            new CreateAccountRequest("Test", "test@test.com", 25, "City", "50.00")
        );
        
        assertThrows(InsufficientFundsException.class, () -> {
            accountService.withdraw(account.accountId(), new AmountRequest("100.00"));
        });
    }
    
    @Test
    void testWithdrawZeroAmount() {
        var account = accountService.createAccount(
            new CreateAccountRequest("Test", "test@test.com", 25, "City", "100.00")
        );
        
        assertThrows(InvalidAmountException.class, () -> {
            accountService.withdraw(account.accountId(), new AmountRequest("0.00"));
        });
    }
    
    @Test
    void testWithdrawFromNonExistentAccount() {
        UUID fakeId = UUID.randomUUID();
        
        assertThrows(AccountNotFoundException.class, () -> {
            accountService.withdraw(fakeId, new AmountRequest("10.00"));
        });
    }
    
    @Test
    void testTransferSuccess() {
        var acc1 = accountService.createAccount(
            new CreateAccountRequest("Alice", "alice@test.com", 25, "City", "100.00")
        );
        var acc2 = accountService.createAccount(
            new CreateAccountRequest("Bob", "bob@test.com", 30, "City", "50.00")
        );
        
        var response = accountService.transfer(
            new TransferRequest(acc1.accountId(), acc2.accountId(), "30.00")
        );
        
        assertNotNull(response);
        assertEquals(acc2.accountId(), response.toAccountId());
        assertEquals("30.00", response.amount());
        assertEquals("70.00", response.resultingBalance());
        assertEquals("80.00", response.recipientBalance());
    }
    
    @Test
    void testTransferToSameAccount() {
        var account = accountService.createAccount(
            new CreateAccountRequest("Test", "test@test.com", 25, "City", "100.00")
        );
        
        assertThrows(InvalidTransferException.class, () -> {
            accountService.transfer(
                new TransferRequest(account.accountId(), account.accountId(), "10.00")
            );
        });
    }
    
    @Test
    void testTransferInsufficientFunds() {
        var acc1 = accountService.createAccount(
            new CreateAccountRequest("Alice", "alice@test.com", 25, "City", "30.00")
        );
        var acc2 = accountService.createAccount(
            new CreateAccountRequest("Bob", "bob@test.com", 30, "City", "50.00")
        );
        
        assertThrows(InsufficientFundsException.class, () -> {
            accountService.transfer(
                new TransferRequest(acc1.accountId(), acc2.accountId(), "50.00")
            );
        });
    }
    
    @Test
    void testTransferSourceNotFound() {
        UUID fakeId = UUID.randomUUID();
        var acc2 = accountService.createAccount(
            new CreateAccountRequest("Bob", "bob@test.com", 30, "City", "50.00")
        );
        
        assertThrows(AccountNotFoundException.class, () -> {
            accountService.transfer(
                new TransferRequest(fakeId, acc2.accountId(), "10.00")
            );
        });
    }
    
    @Test
    void testTransferDestinationNotFound() {
        var acc1 = accountService.createAccount(
            new CreateAccountRequest("Alice", "alice@test.com", 25, "City", "100.00")
        );
        UUID fakeId = UUID.randomUUID();
        
        assertThrows(AccountNotFoundException.class, () -> {
            accountService.transfer(
                new TransferRequest(acc1.accountId(), fakeId, "10.00")
            );
        });
    }
    
    @Test
    void testGetOutgoingTransfersOrder() {
        var acc1 = accountService.createAccount(
            new CreateAccountRequest("Alice", "alice@test.com", 25, "City", "100.00")
        );
        var acc2 = accountService.createAccount(
            new CreateAccountRequest("Bob", "bob@test.com", 30, "City", "50.00")
        );
        
        accountService.transfer(new TransferRequest(acc1.accountId(), acc2.accountId(), "10.00"));
        accountService.transfer(new TransferRequest(acc1.accountId(), acc2.accountId(), "20.00"));
        accountService.transfer(new TransferRequest(acc1.accountId(), acc2.accountId(), "30.00"));
        
        var response = accountService.getOutgoingTransfers(acc1.accountId());
        
        assertEquals(3, response.transfers().size());

        assertEquals("30.00", response.transfers().get(0).amount());
        assertEquals("20.00", response.transfers().get(1).amount());
        assertEquals("10.00", response.transfers().get(2).amount());
    }
}
