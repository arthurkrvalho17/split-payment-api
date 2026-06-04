package com.psp.split_payment_api.application.service;

import com.psp.split_payment_api.domain.exception.MerchantNotFoundException;
import com.psp.split_payment_api.domain.exception.RecipientNotFoundException;
import com.psp.split_payment_api.domain.model.SplitRule;
import com.psp.split_payment_api.domain.repository.MerchantRepository;
import com.psp.split_payment_api.domain.repository.RecipientRepository;
import com.psp.split_payment_api.domain.repository.SplitRuleRepository;
import com.psp.split_payment_api.infra.dto.CreateSplitRuleRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SplitRuleService {

    private final RecipientRepository recipientRepository;
    private final MerchantRepository merchantRepository;
    private final SplitRuleRepository splitRuleRepository;

    public SplitRule save(CreateSplitRuleRequest request) {

        return splitRuleRepository.save(SplitRule.builder()
                .recipient(recipientRepository.findById(request.recipientId())
                        .orElseThrow(() -> new RecipientNotFoundException(request.recipientId())))
                .merchant(merchantRepository.findById(request.merchantId())
                        .orElseThrow(() -> new MerchantNotFoundException(request.merchantId())))
                .percent(request.percent())
                .type(request.type())
                .createdAt(OffsetDateTime.now())
                .build());
    }
}
