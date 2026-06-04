package com.psp.split_payment_api.infra.dto;

import com.psp.split_payment_api.domain.model.SplitType;

import java.util.UUID;

public record SplitEntryResponse(
        UUID recipientId,
        int percentualApplied,
        SplitType type,
        long amountCents
) {
}
