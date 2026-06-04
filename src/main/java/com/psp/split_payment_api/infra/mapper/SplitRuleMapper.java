package com.psp.split_payment_api.infra.mapper;

import com.psp.split_payment_api.domain.model.SplitRule;
import com.psp.split_payment_api.infra.persistence.MerchantEntity;
import com.psp.split_payment_api.infra.persistence.RecipientEntity;
import com.psp.split_payment_api.infra.persistence.SplitRuleEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SplitRuleMapper {

    private final RecipientMapper recipientMapper;
    private final MerchantMapper merchantMapper;

    public SplitRuleEntity toEntity(SplitRule splitRule) {
        return  SplitRuleEntity.builder()
                .recipient(RecipientEntity.builder()
                        .id(splitRule.getRecipient().getId())
                        .build())
                .merchant(MerchantEntity.builder()
                        .id(splitRule.getMerchant().getId())
                        .build())
                .percent(splitRule.getPercent())
                .type(splitRule.getType())
                .createdAt(splitRule.getCreatedAt())
                .build();
    }

    public SplitRule toDomain(SplitRuleEntity splitRuleEntity) {
        return SplitRule.builder()
                .id(splitRuleEntity.getId())
                .recipient(recipientMapper.toDomain(splitRuleEntity.getRecipient()))
                .merchant(merchantMapper.toDomain(splitRuleEntity.getMerchant()))
                .percent(splitRuleEntity.getPercent())
                .type(splitRuleEntity.getType())
                .createdAt(splitRuleEntity.getCreatedAt())
                .build();
    }
}
