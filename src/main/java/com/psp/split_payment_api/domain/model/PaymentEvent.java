package com.psp.split_payment_api.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

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

    private OffsetDateTime createdAt;
}
