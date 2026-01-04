package com.bandhub.zsi.ecommerce.domain;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    private UUID productId;
    private String productName;
    @Embedded
    private Money unitPrice;
    private int quantity;

    public OrderItem(UUID productId, String productName, Money unitPrice, int quantity) {
        this.productId = productId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public Money calculateLineTotal() {
        var totalAmount = unitPrice.amount().multiply(BigDecimal.valueOf(quantity));
        return new Money(totalAmount, unitPrice.currency());
    }
}