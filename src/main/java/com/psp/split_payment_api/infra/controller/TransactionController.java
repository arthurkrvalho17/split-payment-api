package com.psp.split_payment_api.infra.controller;

import com.psp.split_payment_api.application.service.TransactionService;
import com.psp.split_payment_api.domain.repository.MerchantRepository;
import com.psp.split_payment_api.domain.repository.RecipientRepository;
import com.psp.split_payment_api.infra.dto.CreateTransactionRequest;
import com.psp.split_payment_api.infra.dto.SplitEntryResponse;
import com.psp.split_payment_api.infra.dto.TransactionResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/transaction")
public class TransactionController {

    private final MerchantRepository merchantRepository;
    private final RecipientRepository recipientRepository;
    private final TransactionService service;

    @PostMapping
    public ResponseEntity effetuateTransaction(@RequestBody @Valid CreateTransactionRequest request) {

        var transaction = service.save(request);

        List<SplitEntryResponse> splits = transaction.getSplits()
                .stream()
                .map(split -> new SplitEntryResponse(
                        split.getRecipient().getId(),
                        split.getPercentApplied(),
                        split.getType(),
                        split.getAmountCents()
                ))
                .toList();

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new TransactionResponse(
                        transaction.getId(),
                        transaction.getMerchant().getId(),
                        transaction.getAmountCents(),
                        transaction.getStatus(),
                        splits,
                        transaction.getCreatedAt()
                )
        );
    }

    @GetMapping("{id}")
    public ResponseEntity<String> detalhateTransaction(@PathVariable UUID id) {

        return ResponseEntity.status(HttpStatus.OK).body(service.detalhateTransaction(id));
    }

}
