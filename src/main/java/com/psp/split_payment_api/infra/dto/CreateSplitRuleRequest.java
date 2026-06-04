package com.psp.split_payment_api.infra.dto;

import com.psp.split_payment_api.domain.model.SplitType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record CreateSplitRuleRequest(
        @NotNull
        UUID merchantId,
        @NotNull
        UUID recipientId,
        @Positive
        int percent,
        @NotNull
        SplitType type
) {
}
