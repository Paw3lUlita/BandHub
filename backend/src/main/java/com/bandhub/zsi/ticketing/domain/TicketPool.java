package com.bandhub.zsi.ticketing.domain;

import com.bandhub.zsi.shared.Money;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "ticket_pools")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TicketPool {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id", nullable = false)
    private Concert concert;

    private String name;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "price")),
            @AttributeOverride(name = "currency", column = @Column(name = "currency"))
    })
    private Money price;

    @Column(name = "total_quantity")
    private int totalQuantity;

    @Column(name = "remaining_quantity")
    private int remainingQuantity;

    // Konstruktor pakietowy - tworzy tylko Concert
    TicketPool(Concert concert, String name, Money price, int quantity) {
        this.concert = concert;
        this.name = name;
        this.price = price;
        this.totalQuantity = quantity;
        this.remainingQuantity = quantity;
    }

    /**
     * Reserves tickets from the pool for purchase (decrements remaining).
     */
    public void reserve(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (this.remainingQuantity < quantity) {
            throw new IllegalStateException("Not enough tickets in pool: " + this.name);
        }
        this.remainingQuantity -= quantity;
    }
}