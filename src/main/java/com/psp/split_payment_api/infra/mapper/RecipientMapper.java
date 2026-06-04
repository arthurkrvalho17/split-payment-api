package com.psp.split_payment_api.infra.mapper;

import com.psp.split_payment_api.domain.model.Recipient;
import com.psp.split_payment_api.infra.persistence.RecipientEntity;
import org.springframework.stereotype.Component;

@Component
public class RecipientMapper {

    public RecipientEntity toEntity(Recipient recipient) {
        return RecipientEntity.builder()
                .name(recipient.getName())
                .document(recipient.getDocument())
                .bankAccount(recipient.getBankAccount())
                .createdAt(recipient.getCreatedAt())
                .build();
    }

    public Recipient toDomain(RecipientEntity recipientEntity) {
        return Recipient.builder()
                .id(recipientEntity.getId())
                .name(recipientEntity.getName())
                .document(recipientEntity.getDocument())
                .bankAccount(recipientEntity.getBankAccount())
                .createdAt(recipientEntity.getCreatedAt())
                .build();
    }
}
