package revel8.model;

import java.util.UUID;

public record TransferRecord(
    UUID transferId,
    UUID toAccountId,
    long amountInCents,
    long timestampMillis,
    long resultingBalanceInCents
) {
}
