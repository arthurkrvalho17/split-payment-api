package com.psp.split_payment_api.infra.dto;

import com.psp.split_payment_api.domain.model.SplitType;

import java.time.OffsetDateTime;
import java.util.UUID;

public record SplitRuleResponse(
        UUID id,
        UUID merchantId,
        UUID recipientId,
        int percent,
        SplitType type,
        OffsetDateTime createdAt
) {
}
