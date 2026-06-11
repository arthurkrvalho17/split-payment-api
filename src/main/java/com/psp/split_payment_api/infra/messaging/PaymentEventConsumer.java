package com.psp.split_payment_api.infra.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @KafkaListener(topics = "payment-events", groupId = "psp-group")
    public void consume(String message) {
        try {
            JsonNode json = objectMapper.readTree(message);
            if (!json.hasNonNull("eventType")) {
                log.error("Payload sem campo eventType, enviando para DLT: {}", message);
                kafkaTemplate.send("payment-events.DLT", message);
                return;
            }
            String eventType = json.get("eventType").asText();
            log.info("Evento recebido: {}", eventType);
        } catch (Exception e) {
            log.error("Erro ao processar evento: {}", message, e);
            kafkaTemplate.send("payment-events.DLT", message);
        }
    }

    @KafkaListener(topics = "payment-events.DLT", groupId = "psp-group-dlt")
    public void consumeDLT(String message) {
        log.warn("Message on Dead Letter Topic: {}", message);
    }
}
