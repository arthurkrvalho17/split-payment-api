package com.psp.split_payment_api.infra.mapper;

import com.psp.split_payment_api.domain.model.SplitEntry;
import com.psp.split_payment_api.domain.model.Transaction;
import com.psp.split_payment_api.infra.persistence.RecipientEntity;
import com.psp.split_payment_api.infra.persistence.SplitEntryEntity;
import com.psp.split_payment_api.infra.persistence.TransactionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SplitEntryMapper {

    private final RecipientMapper recipientMapper;

    public SplitEntryEntity toEntity(SplitEntry splitEntry) {
        return SplitEntryEntity.builder()
                .transaction((TransactionEntity.builder()
                        .id(splitEntry.getTransaction().getId()).build()))
                .recipient(RecipientEntity.builder()
                        .id(splitEntry.getRecipient().getId())
                        .build())
                .amountCents(splitEntry.getAmountCents())
                .percentApplied(splitEntry.getPercentApplied())
                .type(splitEntry.getType())
                .createdAt(splitEntry.getCreatedAt())
                .build();
    }

    public SplitEntry toDomain(SplitEntryEntity splitEntryEntity) {
        return SplitEntry.builder()
                .id(splitEntryEntity.getId())
                .transaction(Transaction.builder()
                        .id(splitEntryEntity.getTransaction().getId())
                        .build())
                .recipient(recipientMapper.toDomain(splitEntryEntity.getRecipient()))
                .amountCents(splitEntryEntity.getAmountCents())
                .percentApplied(splitEntryEntity.getPercentApplied())
                .type(splitEntryEntity.getType())
                .createdAt(splitEntryEntity.getCreatedAt())
                .build();
    }
}
