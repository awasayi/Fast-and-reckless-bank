package revel8.dto;

import jakarta.validation.constraints.*;

public record CreateAccountRequest(
        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotNull(message = "Age is required")
        @Min(value = 18, message = "Must be at least 18 years old")
        @Max(value = 150, message = "Invalid age")
        Integer age,

        @NotBlank(message = "City is required")
        String city,

        @NotBlank(message = "Initial deposit is required")
        String initialDeposit
) {
}

