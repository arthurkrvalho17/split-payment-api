package com.psp.split_payment_api.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class SplitEntry {

    private UUID id;

    private Transaction transaction;

    private Recipient recipient;

    private Long amountCents;

    private int percentApplied;

    private SplitType type;

    private OffsetDateTime createdAt;
}
