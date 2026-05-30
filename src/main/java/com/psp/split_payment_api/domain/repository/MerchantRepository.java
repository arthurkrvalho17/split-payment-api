package com.psp.split_payment_api.domain.repository;

import com.psp.split_payment_api.domain.model.Merchant;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MerchantRepository {

    Merchant save(Merchant merchant);

    Optional<Merchant> findById(UUID id);

    List<Merchant> findAll();
}
