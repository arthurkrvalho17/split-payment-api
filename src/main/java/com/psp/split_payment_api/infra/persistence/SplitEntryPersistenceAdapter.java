package com.psp.split_payment_api.infra.persistence;

import com.psp.split_payment_api.domain.model.SplitEntry;
import com.psp.split_payment_api.domain.repository.SplitEntryRepository;
import com.psp.split_payment_api.infra.mapper.SplitEntryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SplitEntryPersistenceAdapter implements SplitEntryRepository {

    private final SplitEntryJpaRepository splitEntryJpaRepository;
    private final SplitEntryMapper splitEntryMapper;

    @Override
    public SplitEntry save(SplitEntry splitEntry) {
        var mapper = splitEntryMapper.toEntity(splitEntry);
        return splitEntryMapper.toDomain(splitEntryJpaRepository.save(mapper));
    }

    @Override
    public Optional<SplitEntry> findById(UUID id) {
        return splitEntryJpaRepository.findById(id).map(splitEntryMapper::toDomain);
    }

    @Override
    public List<SplitEntry> findAll() {
        return splitEntryJpaRepository.findAll()
                .stream()
                .map(splitEntryMapper::toDomain)
                .toList();
    }
}
