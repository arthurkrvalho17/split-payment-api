package com.psp.split_payment_api.domain.exception;

import java.util.UUID;

public class MerchantNotFoundException extends NotFoundException{
    public MerchantNotFoundException(UUID id) {
        super("Merchant not found: " + id);
    }
}
