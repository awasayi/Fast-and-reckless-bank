package revel8.dto;

import java.time.Instant;

public record ErrorResponse(
        String error,
        long timestamp
) {
    public ErrorResponse(String error) {
        this(error, Instant.now().toEpochMilli());
    }
}

