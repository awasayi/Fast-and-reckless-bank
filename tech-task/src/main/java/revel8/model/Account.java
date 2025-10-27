package revel8.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import revel8.datastructure.RingBuffer;

import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

@Getter
@Builder
public class Account {
    private static final int STORED_RECORDS_LIMIT = 50;

    private final UUID id;
    private final String name;
    private final String email;
    private final Integer age;
    private final String city;

    @Setter
    private long balanceInCents;
    
    @Builder.Default
    private final ReentrantLock lock = new ReentrantLock();
    
    @Builder.Default
    private final RingBuffer outgoing = new RingBuffer(STORED_RECORDS_LIMIT);
}
