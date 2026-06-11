package com.psp.split_payment_api.infra.persistence;

import com.psp.split_payment_api.domain.model.PaymentEvent;
import com.psp.split_payment_api.domain.model.PaymentEventStatus;
import com.psp.split_payment_api.domain.repository.PaymentEventRepository;
import com.psp.split_payment_api.infra.mapper.PaymentEventMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaymentEventPersistenceAdapter implements PaymentEventRepository {

    private final PaymentEventJpaRepository jpaRepository;
    private final PaymentEventMapper paymentEventMapper;

    @Override
    public PaymentEvent save(PaymentEvent paymentEvent) {
        var mapped = paymentEventMapper.toEntity(paymentEvent);
        return paymentEventMapper.toDomain(jpaRepository.save(mapped));
    }

    @Override
    public Optional<PaymentEvent> findById(UUID paymentEventId) {
        return jpaRepository.findById(paymentEventId).map(paymentEventMapper::toDomain);
    }

    @Override
    public List<PaymentEvent> findByTransactionId(UUID transactionId) {
        return jpaRepository.findByTransaction_Id(transactionId)
                .stream()
                .map(paymentEventMapper::toDomain)
                .toList();
    }

    @Override
    public List<PaymentEvent> findByStatus(PaymentEventStatus status) {
        return jpaRepository.findByStatus(status)
                .stream()
                .map(paymentEventMapper::toDomain)
                .toList();
    }

    @Override
    public List<PaymentEvent> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(paymentEventMapper::toDomain)
                .toList();
    }
}
