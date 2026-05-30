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
public class Merchant {

    private UUID id;

    private String name;

    private String document;

    private List<SplitRule> splitRules;

    private OffsetDateTime createdAt;
}
