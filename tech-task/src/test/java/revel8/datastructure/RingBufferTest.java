package revel8.datastructure;

import org.junit.jupiter.api.Test;
import revel8.model.TransferRecord;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RingBufferTest {
    
    @Test
    void testAppendAndRetrieve() {
        RingBuffer buffer = new RingBuffer(5);
        
        TransferRecord tr1 = new TransferRecord(UUID.randomUUID(), UUID.randomUUID(), 1000, 1L, 9000);
        TransferRecord tr2 = new TransferRecord(UUID.randomUUID(), UUID.randomUUID(), 2000, 2L, 7000);
        
        buffer.append(tr1);
        buffer.append(tr2);
        
        List<TransferRecord> transfers = buffer.getRecentNewestFirst();
        assertEquals(2, transfers.size());
        assertEquals(tr2.transferId(), transfers.get(0).transferId());
        assertEquals(tr1.transferId(), transfers.get(1).transferId());
    }
    
    @Test
    void testNewestFirstOrder() {
        RingBuffer buffer = new RingBuffer(10);
        
        for (int i = 0; i < 5; i++) {
            TransferRecord tr = new TransferRecord(
                UUID.randomUUID(),
                UUID.randomUUID(),
                (i + 1) * 1000,
                i,
                10000 - (i + 1) * 1000
            );
            buffer.append(tr);
        }
        
        List<TransferRecord> transfers = buffer.getRecentNewestFirst();
        
        assertEquals(5, transfers.size());
        assertEquals(5000, transfers.get(0).amountInCents());
        assertEquals(4000, transfers.get(1).amountInCents());
        assertEquals(3000, transfers.get(2).amountInCents());
        assertEquals(2000, transfers.get(3).amountInCents());
        assertEquals(1000, transfers.get(4).amountInCents());
    }
    
    @Test
    void testOverwriteWhenFull() {
        RingBuffer buffer = new RingBuffer(3);
        
        TransferRecord tr1 = new TransferRecord(UUID.randomUUID(), UUID.randomUUID(), 1000, 1L, 9000);
        TransferRecord tr2 = new TransferRecord(UUID.randomUUID(), UUID.randomUUID(), 2000, 2L, 7000);
        TransferRecord tr3 = new TransferRecord(UUID.randomUUID(), UUID.randomUUID(), 3000, 3L, 4000);
        TransferRecord tr4 = new TransferRecord(UUID.randomUUID(), UUID.randomUUID(), 4000, 4L, 0);
        
        buffer.append(tr1);
        buffer.append(tr2);
        buffer.append(tr3);
        buffer.append(tr4);
        
        List<TransferRecord> transfers = buffer.getRecentNewestFirst();
        assertEquals(3, transfers.size());

        assertEquals(tr4.transferId(), transfers.get(0).transferId());
        assertEquals(tr3.transferId(), transfers.get(1).transferId());
        assertEquals(tr2.transferId(), transfers.get(2).transferId());
    }
}

