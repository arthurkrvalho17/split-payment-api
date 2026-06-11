package com.psp.split_payment_api.infra.persistence;

import com.psp.split_payment_api.domain.model.PaymentEventStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PaymentEventJpaRepository extends JpaRepository<PaymentEventEntity, UUID> {
    List<PaymentEventEntity> findByTransaction_Id(UUID transactionId);
    List<PaymentEventEntity> findByStatus(PaymentEventStatus status);
}
