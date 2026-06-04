package com.psp.split_payment_api.infra.controller;

import com.psp.split_payment_api.application.service.MerchantService;
import com.psp.split_payment_api.infra.dto.CreateMerchantRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/merchant")
@RequiredArgsConstructor
public class MerchantController {

    private final MerchantService service;

    @PostMapping
    public ResponseEntity save(@RequestBody @Valid CreateMerchantRequest request) {

        var merchant = service.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(merchant);
    }
}
