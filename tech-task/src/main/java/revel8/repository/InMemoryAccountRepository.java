package revel8.repository;


import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import revel8.model.Account;

@Repository
public class InMemoryAccountRepository {
    private final ConcurrentHashMap<UUID, Account> accounts = new ConcurrentHashMap<>();

    public Account create(String name, String email, Integer age, String city, long initialCents) {
        UUID id = UUID.randomUUID();
        Account acc = Account.builder()
                .id(id)
                .name(name)
                .email(email)
                .age(age)
                .city(city)
                .balanceInCents(initialCents)
                .build();
        
        // extremely unlikely to happen but there is a probability
        Account existing = accounts.putIfAbsent(id, acc);
        if (existing != null) {
            return create(name, email, age, city, initialCents);
        }
        
        return acc;
    }

    public Optional<Account> findById(UUID id) {
        return Optional.ofNullable(accounts.get(id));
    }

    public Collection<Account> findAll() {
        return accounts.values();
    }
}
