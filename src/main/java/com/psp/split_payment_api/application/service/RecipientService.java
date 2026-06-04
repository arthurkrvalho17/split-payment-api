package com.psp.split_payment_api.application.service;

import com.psp.split_payment_api.domain.model.Recipient;
import com.psp.split_payment_api.domain.repository.RecipientRepository;
import com.psp.split_payment_api.infra.dto.CreateRecipientRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RecipientService {

    private final RecipientRepository recipientRepository;

    public Recipient save(CreateRecipientRequest request) {

        return recipientRepository.save(Recipient.builder()
                .name(request.name())
                .document(request.document())
                .bankAccount(request.bankAccount())
                .createdAt(OffsetDateTime.now())
                .build());
    }
}
