package revel8.dto;

import java.util.UUID;

public record CreateAccountResponse(
        UUID accountId,
        String name,
        String email,
        Integer age,
        String city,
        String balance
) {
}

