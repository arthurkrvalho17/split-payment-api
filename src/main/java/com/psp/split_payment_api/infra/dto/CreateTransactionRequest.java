package com.psp.split_payment_api.infra.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record CreateTransactionRequest(
        @NotNull
        UUID merchantId,
        @NotNull
        UUID recipientId,
        @Positive
        long amountCents) {
}
