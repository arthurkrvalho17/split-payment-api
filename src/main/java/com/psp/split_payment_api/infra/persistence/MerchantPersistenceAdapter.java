package com.psp.split_payment_api.infra.persistence;

import com.psp.split_payment_api.domain.model.Merchant;
import com.psp.split_payment_api.domain.repository.MerchantRepository;
import com.psp.split_payment_api.infra.mapper.MerchantMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MerchantPersistenceAdapter implements MerchantRepository{

    private final MerchantJpaRepository jpaRepository;
    private final MerchantMapper merchantMapper;

    @Override
    public Merchant save(Merchant merchant) {
        var mapped = merchantMapper.toEntity(merchant);
        return merchantMapper.toDomain(jpaRepository.save(mapped));
    }

    @Override
    public Optional<Merchant> findById(UUID id)         {
        return jpaRepository.findById(id).map(merchantMapper::toDomain);
    }

    @Override
    public List<Merchant> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(merchantMapper::toDomain)
                .toList();
    }
}
