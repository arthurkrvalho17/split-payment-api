package com.psp.split_payment_api.domain.exception;

import java.util.UUID;

public class NotFoundException extends RuntimeException{
    public NotFoundException(String message) {
        super(message);
    }
}
