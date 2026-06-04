package com.psp.split_payment_api.infra.persistence;

import com.psp.split_payment_api.domain.model.Transaction;
import com.psp.split_payment_api.domain.repository.TransactionRepository;
import com.psp.split_payment_api.infra.mapper.TransactionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TransactionPersistenceAdapter implements TransactionRepository {

    private final TransactionJpaRepository transactionJpaRepository;
    private final TransactionMapper transactionMapper;

    @Override
    public Transaction save(Transaction transaction) {
        var mapped = transactionMapper.toEntity(transaction);
        return transactionMapper.toDomain(transactionJpaRepository.save(mapped));
    }

    @Override
    public Optional<Transaction> findById(UUID id) {
        return transactionJpaRepository.findById(id).map(transactionMapper::toDomain);
    }

    @Override
    public List<Transaction> findAll() {
        return transactionJpaRepository.findAll()
                .stream()
                .map(transactionMapper::toDomain)
                .toList();
    }
}
