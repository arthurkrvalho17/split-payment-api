package com.psp.split_payment_api.infra.controller;

import com.psp.split_payment_api.application.service.RecipientService;
import com.psp.split_payment_api.infra.dto.CreateRecipientRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/recipient")
@RequiredArgsConstructor
public class RecipientController {

    private final RecipientService service;

    @PostMapping
    public ResponseEntity save(@RequestBody @Valid CreateRecipientRequest request) {

        var recipient = service.save(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(recipient);
    }
}
