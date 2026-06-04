package com.psp.split_payment_api.infra.persistence;

import com.psp.split_payment_api.domain.model.SplitRule;
import com.psp.split_payment_api.domain.repository.SplitRuleRepository;
import com.psp.split_payment_api.infra.mapper.SplitRuleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SplitRulePersistenceAdapter implements SplitRuleRepository {

    private final SplitRuleJpaRepository splitRuleJpaRepository;
    private final SplitRuleMapper splitRuleMapper;

    @Override
    public SplitRule save(SplitRule splitRule) {
        var mapped = splitRuleMapper.toEntity(splitRule);
        return (splitRuleMapper.toDomain(splitRuleJpaRepository.save(mapped)));
    }

    @Override
    public Optional<SplitRule> findById(UUID id) {
        return splitRuleJpaRepository.findById(id).map(splitRuleMapper::toDomain);
    }

    @Override
    public List<SplitRule> findByMerchant(UUID merchantId) {
        return splitRuleJpaRepository.findByMerchant_Id(merchantId)
                .stream()
                .map(splitRuleMapper::toDomain)
                .toList();
    }

    @Override
    public List<SplitRule> findAll() {
        return splitRuleJpaRepository.findAll()
                .stream()
                .map(splitRuleMapper::toDomain)
                .toList();
    }
}
