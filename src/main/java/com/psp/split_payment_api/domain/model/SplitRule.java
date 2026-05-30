package com.psp.split_payment_api.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class SplitRule {

    private UUID id;

    private Recipient recipient;

    private Merchant merchant;

    private int percent;

    private SplitType type;

    private OffsetDateTime createdAt;
}
