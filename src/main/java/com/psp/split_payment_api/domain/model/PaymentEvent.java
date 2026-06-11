package com.psp.split_payment_api.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.With;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class PaymentEvent {

    private UUID id;

    private Transaction transaction;

    private EventType eventType;

    private String payload;

    @With
    private PaymentEventStatus status;

    private OffsetDateTime createdAt;
}
