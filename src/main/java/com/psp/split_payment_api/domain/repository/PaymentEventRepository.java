package com.psp.split_payment_api.domain.repository;

import com.psp.split_payment_api.domain.model.PaymentEvent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentEventRepository {

    PaymentEvent save(PaymentEvent paymentEvent);

    Optional<PaymentEvent> findById(UUID paymentEventId);

    List<PaymentEvent> findByTransactionId(UUID transactionId);

    List<PaymentEvent> findAll();
}
