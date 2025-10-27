package revel8.dto;

import java.util.UUID;

public record TransferResponse(
        UUID transferId,
        UUID toAccountId,
        String amount,
        long timestampMillis,
        String resultingBalance,
        String recipientBalance
) {
}

