package com.psp.split_payment_api.infra.dto;

import com.psp.split_payment_api.domain.model.TransactionStatus;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record TransactionResponse(
        UUID id,
        UUID merchantId,
        long amountCents,
        TransactionStatus status,
        List<SplitEntryResponse> splits,
        OffsetDateTime createdAt
) {
}
