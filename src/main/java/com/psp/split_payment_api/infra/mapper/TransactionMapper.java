package com.psp.split_payment_api.infra.mapper;

import com.psp.split_payment_api.domain.model.Transaction;
import com.psp.split_payment_api.infra.persistence.MerchantEntity;
import com.psp.split_payment_api.infra.persistence.TransactionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TransactionMapper {

    private final MerchantMapper merchantMapper;
    private final SplitEntryMapper splitEntryMapper;

    public TransactionEntity toEntity(Transaction transaction) {
        return TransactionEntity.builder()
                .id(transaction.getId())
                .merchant(MerchantEntity.builder()
                        .id(transaction.getMerchant().getId())
                        .build())
                .amountCents(transaction.getAmountCents())
                .status(transaction.getStatus())
                .splits(List.of())
                .createdAt(transaction.getCreatedAt())
                .build();
    }

    public Transaction toDomain(TransactionEntity transactionEntity) {
        return Transaction.builder()
                .id(transactionEntity.getId())
                .merchant(merchantMapper.toDomain(transactionEntity.getMerchant()))
                .amountCents(transactionEntity.getAmountCents())
                .status(transactionEntity.getStatus())
                .splits((transactionEntity.getSplits()
                        .stream()
                        .map(splitEntryMapper::toDomain)
                        .toList()))
                .createdAt(transactionEntity.getCreatedAt()).build();
    }
}
