package com.psp.split_payment_api.infra.persistence;

import com.psp.split_payment_api.domain.model.SplitRule;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "merchant")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MerchantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    private String document;

    @OneToMany (mappedBy = "merchant")
    private List<SplitRuleEntity> splitRules;

    private OffsetDateTime createdAt;
}
