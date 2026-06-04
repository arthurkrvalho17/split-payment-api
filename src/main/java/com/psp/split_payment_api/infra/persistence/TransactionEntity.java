package com.psp.split_payment_api.infra.persistence;

import com.psp.split_payment_api.domain.model.TransactionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "transaction")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "merchant_id")
    private MerchantEntity merchant;

    private Long amountCents;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    //Forçar o carregamento das splits
    @OneToMany (mappedBy = "transaction", fetch = FetchType.EAGER)
    private List<SplitEntryEntity> splits;

    private OffsetDateTime createdAt;

}
