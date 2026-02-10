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
    TicketPool(String name, Money price, int quantity) {
        this.name = name;
        this.price = price;
        this.totalQuantity = quantity;
        this.remainingQuantity = quantity;
    }
}