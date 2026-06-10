package com.psp.split_payment_api.application.service;

import com.psp.split_payment_api.domain.model.Merchant;
import com.psp.split_payment_api.domain.repository.MerchantRepository;
import com.psp.split_payment_api.infra.dto.CreateMerchantRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MerchantService {

    private final MerchantRepository merchantRepository;

    public Merchant save(CreateMerchantRequest request) {

        return merchantRepository.save(Merchant.builder()
                .name(request.name())
                .document(request.document())
                .createdAt(OffsetDateTime.now())
                .splitRules(List.of())
                .build());
    }
}
