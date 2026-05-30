package com.psp.split_payment_api.domain.repository;

import com.psp.split_payment_api.domain.model.Recipient;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RecipientRepository {

    Recipient save(Recipient recipient);

    Optional<Recipient> findById(UUID id);

    List<Recipient> findAll();
}
