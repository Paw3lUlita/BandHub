package com.bandhub.zsi.ticketing.domain;

import com.bandhub.zsi.shared.Money;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ticket_orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TicketOrder {

    @Id
    private UUID id;

    @Column(name = "user_id")
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id", nullable = false)
    private Concert concert;

    private String status;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "total_amount")),
            @AttributeOverride(name = "currency", column = @Column(name = "currency"))
    })
    private Money totalAmount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public static TicketOrder create(UUID id, String userId, Concert concert, String status, Money totalAmount) {
        TicketOrder order = new TicketOrder();
        order.id = id;
        order.userId = userId;
        order.concert = concert;
        order.status = status;
        order.totalAmount = totalAmount;
        order.createdAt = LocalDateTime.now();
        return order;
    }

    public void update(String userId, Concert concert, String status, Money totalAmount) {
        this.userId = userId;
        this.concert = concert;
        this.status = status;
        this.totalAmount = totalAmount;
    }
}
