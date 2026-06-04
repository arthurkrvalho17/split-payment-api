import com.psp.split_payment_api.application.service.TransactionService;
import com.psp.split_payment_api.domain.model.*;
import com.psp.split_payment_api.domain.repository.*;
import com.psp.split_payment_api.infra.dto.CreateSplitRuleRequest;
import com.psp.split_payment_api.infra.dto.CreateTransactionRequest;
import com.psp.split_payment_api.infra.messaging.PaymentEventProducer;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.*;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {
    @Mock
    private MerchantRepository merchantRepository;
    @Mock
    private SplitRuleRepository splitRuleRepository;
    @Mock
    private SplitEntryRepository splitEntryRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private RecipientRepository recipientRepository;
    @Mock
    private PaymentEventRepository paymentEventRepository;
    @Mock
    private PaymentEventProducer paymentEventProducer;
    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    void setUp() throws Exception {
        var field = TransactionService.class.getDeclaredField("entityManager");
        field.setAccessible(true);
        field.set(transactionService, entityManager);
    }

    @Test//Teste dos cálculos de spits
    void shouldCalculateSplitCorrectly() {

        var merchantId = UUID.randomUUID();
        var recipientId = UUID.randomUUID();

        var merchant = Merchant.builder()
                .id(merchantId)
                .name("iFood")
                .document("12345678000100")
                .splitRules(List.of())
                .createdAt(OffsetDateTime.now())
                .build();


        var recipient = Recipient.builder()
                .id(recipientId)
                .name("Restaurante X")
                .document("98765432000100")
                .bankAccount("AG001 CC12345")
                .createdAt(OffsetDateTime.now())
                .build();

        var splitRules = List.of(
                SplitRule.builder()
                        .id(UUID.randomUUID())
                        .merchant(merchant)
                        .recipient(recipient)
                        .percent(10)
                        .type(SplitType.FEE)
                        .createdAt(OffsetDateTime.now())
                        .build()
        );

        var request = new CreateTransactionRequest(merchantId, recipientId, 10000L);

        when(merchantRepository.findById(merchantId)).thenReturn(Optional.of(merchant));
        when(recipientRepository.findById(recipientId)).thenReturn(Optional.of(recipient));
        when(splitRuleRepository.findByMerchant(merchantId)).thenReturn(splitRules);
        when(transactionRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(splitEntryRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(paymentEventRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(transactionRepository.findById(any())).thenReturn(Optional.of(
                Transaction.builder()
                        .id(UUID.randomUUID())
                        .merchant(merchant)
                        .amountCents(10000L)
                        .status(TransactionStatus.COMPLETED)
                        .splits(List.of())
                        .createdAt(OffsetDateTime.now())
                        .build()
        ));

        var result = transactionService.save(request);

        assertNotNull(result);
        assertEquals(TransactionStatus.COMPLETED, result.getStatus());
        assertEquals(10000L, result.getAmountCents());
    }

    @Test //Merchant ñ encontrado
    void shouldThrowExceptionWhenMerchantNotFound() {
        var request = new CreateTransactionRequest(UUID.randomUUID(), UUID.randomUUID(), 10000L);

        when(merchantRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> transactionService.save(request));
    }

    @Test
    void shouldReturnTheCorrectlyAmount() {
        var merchant = Merchant.builder()
                .id(UUID.randomUUID())
                .name("iFood")
                .document("12345678000100")
                .splitRules(List.of())
                .createdAt(OffsetDateTime.now())
                .build();

        var recipient = Recipient.builder()
                .id(UUID.randomUUID())
                .name("Restaurante X")
                .document("98765432000100")
                .bankAccount("AG001 CC12345")
                .createdAt(OffsetDateTime.now())
                .build();

        var splitRules = List.of(
                SplitRule.builder()
                        .id(UUID.randomUUID())
                        .merchant(merchant)
                        .recipient(recipient)
                        .percent(10)
                        .type(SplitType.FEE)
                        .createdAt(OffsetDateTime.now())
                        .build()
        );

        var request = new CreateTransactionRequest(merchant.getId(), recipient.getId(), 1000L);

        when(merchantRepository.findById(merchant.getId())).thenReturn(Optional.of(merchant));
        when(recipientRepository.findById(recipient.getId())).thenReturn(Optional.of(recipient));
        when(splitRuleRepository.findByMerchant(merchant.getId())).thenReturn(splitRules);
        when(transactionRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(splitEntryRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(paymentEventRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(transactionRepository.findById(any())).thenReturn(Optional.of(
                Transaction.builder()
                        .id(UUID.randomUUID())
                        .merchant(merchant)
                        .amountCents(1000L)
                        .status(TransactionStatus.COMPLETED)
                        .splits(List.of())
                        .createdAt(OffsetDateTime.now())
                        .build()
        ));

        transactionService.save(request);

        var captor = ArgumentCaptor.forClass(SplitEntry.class);
        verify(splitEntryRepository, atLeastOnce()).save(captor.capture());

        var feeEntry = captor.getAllValues().stream()
                .filter(s -> s.getType() == SplitType.FEE)
                .findFirst()
                .orElseThrow();

        assertEquals(100L, feeEntry.getAmountCents());
        assertEquals(10, feeEntry.getPercentApplied());

    }

}
