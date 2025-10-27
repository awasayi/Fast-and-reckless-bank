package revel8.datastructure;

import revel8.model.TransferRecord;

import java.util.ArrayList;
import java.util.List;

public class RingBuffer {
    private final TransferRecord[] buffer;
    private int nextIndex = 0;
    private int count = 0;

    public RingBuffer(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        this.buffer = new TransferRecord[capacity];
    }

    public void append(TransferRecord transfer) {
        buffer[nextIndex] = transfer;
        nextIndex = (nextIndex + 1) % buffer.length;
        if (count < buffer.length) {
            count++;
        }
    }

    public List<TransferRecord> getRecentNewestFirst() {
        List<TransferRecord> result = new ArrayList<>(count);
        int index = decrementIndex(nextIndex);
        
        for (int i = 0; i < count; i++) {
            result.add(buffer[index]);
            index = decrementIndex(index);
        }
        return result;
    }

    private int decrementIndex(int index) {
        return (index - 1 + buffer.length) % buffer.length;
    }
}

