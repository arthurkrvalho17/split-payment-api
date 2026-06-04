package com.psp.split_payment_api.domain.exception;

import java.util.UUID;

public class TransactionNotFoundException extends NotFoundException{
    public TransactionNotFoundException(UUID id) {
        super("Transaction not found: " + id);
    }
}
