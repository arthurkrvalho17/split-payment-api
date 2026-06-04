package com.psp.split_payment_api.domain.exception;

import java.util.UUID;

public class SplitRuleNotFoundException extends NotFoundException{
    public SplitRuleNotFoundException(UUID id) {
        super("Split Rule not found: " + id);
    }
}
