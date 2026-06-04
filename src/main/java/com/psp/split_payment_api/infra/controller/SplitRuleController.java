package com.psp.split_payment_api.infra.controller;

import com.psp.split_payment_api.application.service.SplitRuleService;
import com.psp.split_payment_api.infra.dto.CreateSplitRuleRequest;
import com.psp.split_payment_api.infra.dto.SplitRuleResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/splitrule")
@RequiredArgsConstructor
public class SplitRuleController {

    private final SplitRuleService service;

    @PostMapping
    public ResponseEntity save(@RequestBody @Valid CreateSplitRuleRequest request) {

        var splitRule = service.save(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new SplitRuleResponse(
                splitRule.getId(),
                splitRule.getMerchant().getId(),
                splitRule.getRecipient().getId(),
                splitRule.getPercent(),
                splitRule.getType(),
                splitRule.getCreatedAt()
        ));
    }
}
