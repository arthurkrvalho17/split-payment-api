package com.psp.split_payment_api.application.service;

import com.psp.split_payment_api.domain.exception.MerchantNotFoundException;
import com.psp.split_payment_api.domain.exception.RecipientNotFoundException;
import com.psp.split_payment_api.domain.exception.TransactionNotFoundException;
import com.psp.split_payment_api.domain.model.*;
import com.psp.split_payment_api.domain.repository.*;
import com.psp.split_payment_api.infra.dto.CreateTransactionRequest;
import com.psp.split_payment_api.infra.messaging.PaymentEventProducer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    @PersistenceContext
    private EntityManager entityManager;
    private final PaymentEventRepository paymentEventRepository;
    private final MerchantRepository merchantRepository;
    private final SplitRuleRepository splitRuleRepository;
    private final SplitEntryRepository splitEntryRepository;
    private final TransactionRepository transactionRepository;
    private final RecipientRepository recipientRepository;
    private final PaymentEventProducer paymentEventProducer;

    @Transactional
    public Transaction save(CreateTransactionRequest request) {

       var transactionPending = transactionRepository.save(Transaction.builder()
            .merchant(merchantRepository.findById(request.merchantId())
                    .orElseThrow(() -> new MerchantNotFoundException(request.merchantId())))
            .amountCents(request.amountCents())
            .status(TransactionStatus.PENDING)
            .splits(List.of())
            .createdAt(OffsetDateTime.now()).build());

       String payload = String.format(
       "{\"transactionId\": \"%s\", \"amountCents\": %d, \"merchantId\": \"%s\"}",

               transactionPending.getId(),
               transactionPending.getAmountCents(),
               transactionPending.getMerchant().getId()
       );

       paymentEventProducer.publish(paymentEventRepository.save(PaymentEvent.builder()
               .eventType(EventType.TRANSACTION_CREATED)
               .transaction(transactionPending)
               .payload(payload)
               .createdAt(OffsetDateTime.now())
               .build()));

       List<SplitRule> splitRules = splitRuleRepository.findByMerchant(request.merchantId());


        long totalDeducted = splitRules.stream()
                .mapToLong(splitRule -> {
                    long amount = Math.round((splitRule.getPercent() * request.amountCents())/100.0);
                    splitEntryRepository.save(SplitEntry.builder()
                            .transaction(transactionPending)
                            .recipient(splitRule.getRecipient())
                            .amountCents(amount)
                            .percentApplied(splitRule.getPercent())
                            .type(splitRule.getType())
                            .createdAt(OffsetDateTime.now())
                            .build());

                    paymentEventProducer.publish(paymentEventRepository.save(PaymentEvent.builder()
                            .eventType(EventType.SPLIT_EXECUTED)
                            .payload(payload)
                            .transaction(transactionPending)
                            .createdAt(OffsetDateTime.now())
                            .build()));

                    return amount;
                })
                .sum();

        int totalPercentDeducted = splitRules.stream()
                        .mapToInt(SplitRule::getPercent)
                        .sum();


        int liquidPercent = 100 - totalPercentDeducted;
        long liquidAmount = request.amountCents() - totalDeducted;

        splitEntryRepository.save(SplitEntry.builder()
                .transaction(transactionPending)
                .recipient(recipientRepository.findById(request.recipientId())
                        .orElseThrow(() -> new RecipientNotFoundException(request.recipientId())))
                .amountCents(liquidAmount)
                .percentApplied(liquidPercent)
                .type(SplitType.TRANSFER)
                .createdAt(OffsetDateTime.now())
                .build());


        var transactionCompleted = transactionRepository.save(transactionPending.toBuilder()
                .status(TransactionStatus.COMPLETED)
                .build());

        paymentEventProducer.publish(paymentEventRepository.save(PaymentEvent.builder()
                .id(UUID.randomUUID())
                .eventType(EventType.TRANSACTION_COMPLETED)
                .payload(payload)
                .transaction(transactionCompleted)
                .createdAt(OffsetDateTime.now())
                .build()));


        //Forçar carregamento de objeto atualizado para buscar as splits
        entityManager.flush();
        entityManager.clear();

        //Sem tratamento pois não é um erro possível
        return transactionRepository.findById(transactionPending.getId())
                .orElseThrow(RuntimeException::new);
    }

    public String detalhateTransaction(UUID transactionId) {

        var transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException(transactionId));

        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

        BigDecimal valueFormated = BigDecimal.valueOf(transaction.getAmountCents()).divide(BigDecimal.valueOf(100));
        StringBuilder splits = new StringBuilder();
        transaction.getSplits().forEach(s -> {
            splits.append(String.format("%-12s Recipient: %-36s | %3d%% | %s\n",
                    "[" + s.getType() + "]",
                    s.getRecipient().getName(),
                    s.getPercentApplied(),
                    formatter.format(BigDecimal.valueOf(s.getAmountCents()).divide(BigDecimal.valueOf(100))))
            );
        });


        return "=== EXTRATO DE TRANSAÇÃO ===\n" +
                "ID: " + transaction.getId() + "\n" +
                "Data: " + transaction.getCreatedAt() + "\n" +
                "Status: " + transaction.getStatus() + "\n" +
                "Valor Total: R$" + valueFormated + "\n" +
                "--- SPLITS ---\n" +
                splits +
                "============================";
    }
}
