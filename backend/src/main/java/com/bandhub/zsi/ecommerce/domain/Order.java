package com.bandhub.zsi.ecommerce.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "user_id")
    private String userId; // ID usera z Keycloak

    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "total_amount")),
            @AttributeOverride(name = "currency", column = @Column(name = "currency"))
    })
    private Money totalAmount;

    // Elementy zamówienia są trzymane w osobnej tabeli, ale zarządzane przez Order
    @ElementCollection
    @CollectionTable(name = "order_items", joinColumns = @JoinColumn(name = "order_id"))
    private List<OrderItem> items = new ArrayList<>();

    // Factory method
    public static Order create(String userId, List<OrderItem> items) {
        Order order = new Order();
        order.id = UUID.randomUUID();
        order.userId = userId;
        order.createdAt = LocalDateTime.now();
        order.status = OrderStatus.NEW;
        order.items = items;
        order.calculateTotal();
        return order;
    }

    private void calculateTotal() {
        if (items.isEmpty()) {
            this.totalAmount = Money.pln(0);
            return;
        }
        // Sumowanie BigDecimal
        var sum = items.stream()
                .map(OrderItem::calculateLineTotal)
                .map(Money::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        this.totalAmount = new Money(sum, items.get(0).getUnitPrice().currency());
    }

    public void markAsShipped() {
        this.status = OrderStatus.SHIPPED;
    }
}