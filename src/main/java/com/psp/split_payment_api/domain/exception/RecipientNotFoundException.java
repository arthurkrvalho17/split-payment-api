package com.psp.split_payment_api.domain.exception;

import java.util.UUID;

public class RecipientNotFoundException extends NotFoundException{
    public RecipientNotFoundException(UUID id) {
        super("Recipient not found: " + id);
    }
}
