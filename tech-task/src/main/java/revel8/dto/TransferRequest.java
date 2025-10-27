package revel8.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record TransferRequest(
        @NotNull(message = "Source account is required")
        UUID fromAccountId,

        @NotNull(message = "Destination account is required")
        UUID toAccountId,

        @NotBlank(message = "Amount is required")
        String amount
) {
}

