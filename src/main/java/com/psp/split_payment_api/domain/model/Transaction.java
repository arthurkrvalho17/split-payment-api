package com.psp.split_payment_api.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class Transaction {

    private UUID id;

    private Merchant merchant;

    private Long amountCents;

    private TransactionStatus status;

    private List<SplitEntry> splits;

    private OffsetDateTime createdAt;
}
