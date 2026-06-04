    package com.psp.split_payment_api.infra.persistence;

    import com.psp.split_payment_api.domain.model.Recipient;
    import com.psp.split_payment_api.domain.repository.RecipientRepository;
    import com.psp.split_payment_api.infra.mapper.RecipientMapper;
    import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Component;

    import java.util.List;
    import java.util.Optional;
    import java.util.UUID;

    @Component
    @RequiredArgsConstructor
    public class RecipientPersistenceAdapter implements RecipientRepository {

        private final RecipientJpaRepository recipientJpaRepository;
        private final RecipientMapper recipientMapper;

        @Override
        public Recipient save(Recipient recipient) {
            var mapped = recipientMapper.toEntity(recipient);
            return recipientMapper.toDomain(recipientJpaRepository.save(mapped));
        }

        @Override
        public Optional<Recipient> findById(UUID id) {
            return recipientJpaRepository.findById(id).map(recipientMapper::toDomain);
        }

        @Override
        public List<Recipient> findAll() {
            return recipientJpaRepository.findAll()
                    .stream()
                    .map(recipientMapper::toDomain)
                    .toList();
        }
    }
