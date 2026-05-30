package com.psp.split_payment_api.domain.repository;

import com.psp.split_payment_api.domain.model.SplitEntry;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SplitEntryRepository {

    SplitEntry save(SplitEntry splitEntry);

    Optional<SplitEntry> findById(UUID id);

    List<SplitEntry> findAll();
}
