package com.bandhub.zsi.ecommerce.domain;

import com.bandhub.zsi.shared.Money;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    private String provider;

    @Column(name = "provider_payment_id")
    private String providerPaymentId;

    private String status;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "amount")),
            @AttributeOverride(name = "currency", column = @Column(name = "currency"))
    })
    private Money amount;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public static Payment create(UUID id, Order order, String provider, String providerPaymentId, String status, Money amount, LocalDateTime paidAt) {
        Payment payment = new Payment();
        payment.id = id;
        payment.order = order;
        payment.provider = provider;
        payment.providerPaymentId = providerPaymentId;
        payment.status = status;
        payment.amount = amount;
        payment.paidAt = paidAt;
        payment.createdAt = LocalDateTime.now();
        return payment;
    }

    public void update(String provider, String providerPaymentId, String status, Money amount, LocalDateTime paidAt) {
        this.provider = provider;
        this.providerPaymentId = providerPaymentId;
        this.status = status;
        this.amount = amount;
        this.paidAt = paidAt;
    }
}
