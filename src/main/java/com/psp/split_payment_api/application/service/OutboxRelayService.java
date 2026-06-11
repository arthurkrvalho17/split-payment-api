package com.psp.split_payment_api.application.service;

import com.psp.split_payment_api.domain.model.PaymentEventStatus;
import com.psp.split_payment_api.domain.repository.PaymentEventRepository;
import com.psp.split_payment_api.infra.messaging.PaymentEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxRelayService {

    private final PaymentEventRepository paymentEventRepository;
    private final PaymentEventProducer paymentEventProducer;

    @Scheduled(fixedDelay = 1000)
    public void relay() {
        var pending = paymentEventRepository.findByStatus(PaymentEventStatus.PENDING);

        pending.forEach(event -> {
            try {
                paymentEventProducer.publish(event);
                paymentEventRepository.save(event.withStatus(PaymentEventStatus.PUBLISHED));
            } catch (Exception e) {
                log.error("Falha ao publicar evento id={}", event.getId(), e);
                paymentEventRepository.save(event.withStatus(PaymentEventStatus.FAILED));
            }
        });
    }
}
