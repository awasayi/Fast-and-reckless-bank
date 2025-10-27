package revel8.dto;

import jakarta.validation.constraints.NotBlank;

public record AmountRequest(
        @NotBlank(message = "Amount is required")
        String amount
) {
}
