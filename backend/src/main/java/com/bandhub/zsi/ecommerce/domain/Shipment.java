package com.bandhub.zsi.ecommerce.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "shipments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Shipment {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    private String carrier;

    @Column(name = "tracking_number")
    private String trackingNumber;

    private String status;

    @Column(name = "shipped_at")
    private LocalDateTime shippedAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public static Shipment create(UUID id, Order order, String carrier, String trackingNumber, String status, LocalDateTime shippedAt, LocalDateTime deliveredAt) {
        Shipment shipment = new Shipment();
        shipment.id = id;
        shipment.order = order;
        shipment.carrier = carrier;
        shipment.trackingNumber = trackingNumber;
        shipment.status = status;
        shipment.shippedAt = shippedAt;
        shipment.deliveredAt = deliveredAt;
        shipment.createdAt = LocalDateTime.now();
        return shipment;
    }

    public void update(String carrier, String trackingNumber, String status, LocalDateTime shippedAt, LocalDateTime deliveredAt) {
        this.carrier = carrier;
        this.trackingNumber = trackingNumber;
        this.status = status;
        this.shippedAt = shippedAt;
        this.deliveredAt = deliveredAt;
    }
}
