package com.psp.split_payment_api.infra.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateMerchantRequest(
        @NotBlank
        String name,
        @NotBlank
        String document
) {
}
