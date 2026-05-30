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
@Table(name = "split_rule")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SplitRuleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private RecipientEntity recipient;

    @ManyToOne
    private MerchantEntity merchant;

    private int percent;

    @Enumerated(EnumType.STRING)
    private SplitType type;

    private OffsetDateTime createdAt;
}
