package com.psp.split_payment_api.domain.repository;

import com.psp.split_payment_api.domain.model.SplitRule;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SplitRuleRepository {

    SplitRule save(SplitRule splitRule);

    Optional<SplitRule> findById(UUID id);

    List<SplitRule> findByMerchant(UUID merchantId);

    List<SplitRule> findAll();
}
