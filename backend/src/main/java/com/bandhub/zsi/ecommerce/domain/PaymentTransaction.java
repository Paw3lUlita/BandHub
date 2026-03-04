package com.bandhub.zsi.ecommerce.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payment_transactions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentTransaction {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Column(name = "event_type")
    private String eventType;

    @Column(name = "external_status")
    private String externalStatus;

    @Column(columnDefinition = "TEXT")
    private String payload;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public static PaymentTransaction create(Payment payment, String eventType, String externalStatus, String payload) {
        PaymentTransaction transaction = new PaymentTransaction();
        transaction.payment = payment;
        transaction.eventType = eventType;
        transaction.externalStatus = externalStatus;
        transaction.payload = payload;
        transaction.createdAt = LocalDateTime.now();
        return transaction;
    }

    public void update(String eventType, String externalStatus, String payload) {
        this.eventType = eventType;
        this.externalStatus = externalStatus;
        this.payload = payload;
    }
}
