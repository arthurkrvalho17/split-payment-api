package com.psp.split_payment_api.infra.mapper;

import com.psp.split_payment_api.domain.model.Merchant;
import com.psp.split_payment_api.infra.persistence.MerchantEntity;
import org.springframework.stereotype.Component;

@Component
public class MerchantMapper {

    public MerchantEntity toEntity(Merchant merchant){
        return MerchantEntity.builder()
                .name(merchant.getName())
                .document(merchant.getDocument())
                .createdAt(merchant.getCreatedAt())
                .build();
    }

    public Merchant toDomain(MerchantEntity merchantEntity){
        return Merchant.builder()
                .id(merchantEntity.getId())
                .name(merchantEntity.getName())
                .document(merchantEntity.getDocument())
                .createdAt(merchantEntity.getCreatedAt())
                .build();
    }
}
