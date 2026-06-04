package com.psp.split_payment_api.infra.messaging;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventConsumer {

    @KafkaListener(topics = "payment-events", groupId = "psp-group")
    public void consume(String message) {
        System.out.println("Evento recebido: " + message);
    }
}
