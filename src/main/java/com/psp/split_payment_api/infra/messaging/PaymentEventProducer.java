package com.psp.split_payment_api.infra.messaging;

import com.psp.split_payment_api.domain.model.PaymentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void publish(PaymentEvent paymentEvent) {
        kafkaTemplate.send("payment-events", paymentEvent.getId().toString(), paymentEvent.getPayload());
    }
}
