package revel8.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import revel8.model.Account;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryAccountRepositoryTest {
    
    private InMemoryAccountRepository repository;
    
    @BeforeEach
    void setUp() {
        repository = new InMemoryAccountRepository();
    }
    
    @Test
    void testCreateAccount() {
        Account account = repository.create("Test User", "test@example.com", 30, "Test City", 10000);
        
        assertNotNull(account);
        assertNotNull(account.getId());
        assertEquals(10000, account.getBalanceInCents());
        assertEquals("Test User", account.getName());
    }
    
    @Test
    void testFindByIdSuccess() {
        Account account = repository.create("Test User", "test@example.com", 30, "Test City", 10000);
        
        Optional<Account> found = repository.findById(account.getId());
        
        assertTrue(found.isPresent());
        assertEquals(account.getId(), found.get().getId());
        assertEquals(10000, found.get().getBalanceInCents());
    }
    
    @Test
    void testFindByIdNotFound() {
        UUID fakeId = UUID.randomUUID();
        
        Optional<Account> found = repository.findById(fakeId);
        
        assertTrue(found.isEmpty());
    }
}

