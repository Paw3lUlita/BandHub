package com.bandhub.zsi.ecommerce.domain;

import jakarta.persistence.Embeddable;
import java.math.BigDecimal;

// To jest Value Object - niemutowalny obiekt wartości.
// @Embeddable oznacza, że jego pola "wkleją się" do tabeli rodzica.
@Embeddable
public record Money(BigDecimal amount, String currency) {

    public Money {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (currency == null) {
            throw new IllegalArgumentException("Currency cannot be null");
        }
    }

    public static Money pln(double amount) {
        return new Money(BigDecimal.valueOf(amount), "PLN");
    }
}