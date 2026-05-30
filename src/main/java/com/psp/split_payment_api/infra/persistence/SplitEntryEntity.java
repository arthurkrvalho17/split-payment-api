package com.psp.split_payment_api.infra.persistence;

import com.psp.split_payment_api.domain.model.SplitType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "split_entry")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SplitEntryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "transaction_id")
    private TransactionEntity transaction;

    @ManyToOne
    @JoinColumn(name = "recipient_id")
    private RecipientEntity recipient;

    private Long amountCents;

    private int percentApplied;

    @Enumerated(EnumType.STRING)
    private SplitType type;

    private OffsetDateTime createdAt;


}
