package com.psp.split_payment_api.infra.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RecipientJpaRepository extends JpaRepository<RecipientEntity, UUID> {
}
