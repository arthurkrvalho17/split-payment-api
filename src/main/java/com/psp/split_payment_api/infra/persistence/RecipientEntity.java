package com.psp.split_payment_api.infra.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "recipient")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipientEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    private String document;

    private String bankAccount;

    private OffsetDateTime createdAt;
}
