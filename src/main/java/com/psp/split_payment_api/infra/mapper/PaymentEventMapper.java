package com.psp.split_payment_api.infra.mapper;

import com.psp.split_payment_api.domain.model.PaymentEvent;
import com.psp.split_payment_api.domain.model.PaymentEventStatus;
import com.psp.split_payment_api.domain.model.Transaction;
import com.psp.split_payment_api.infra.persistence.PaymentEventEntity;
import com.psp.split_payment_api.infra.persistence.TransactionEntity;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventMapper {

    public PaymentEventEntity toEntity(PaymentEvent paymentEvent) {
        return PaymentEventEntity.builder()
                .transaction(TransactionEntity.builder()
                        .id(paymentEvent.getTransaction().getId())
                        .build())
                .eventType(paymentEvent.getEventType())
                .payload(paymentEvent.getPayload())
                .status(paymentEvent.getStatus() != null ? paymentEvent.getStatus() : PaymentEventStatus.PENDING)
                .createdAt(paymentEvent.getCreatedAt())
                .build();
    }

    public PaymentEvent toDomain(PaymentEventEntity paymentEventEntity) {
        return PaymentEvent.builder()
                .id(paymentEventEntity.getId())
                .transaction(Transaction.builder()
                        .id(paymentEventEntity.getTransaction().getId())
                        .build())
                .eventType(paymentEventEntity.getEventType())
                .payload(paymentEventEntity.getPayload())
                .status(paymentEventEntity.getStatus())
                .createdAt(paymentEventEntity.getCreatedAt())
                .build();
    }
}
