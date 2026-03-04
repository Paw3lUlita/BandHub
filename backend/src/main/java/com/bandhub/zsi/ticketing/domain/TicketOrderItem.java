package com.bandhub.zsi.ticketing.domain;

import com.bandhub.zsi.shared.Money;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "ticket_order_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TicketOrderItem {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_order_id", nullable = false)
    private TicketOrder ticketOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_pool_id", nullable = false)
    private TicketPool ticketPool;

    private int quantity;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "unit_price")),
            @AttributeOverride(name = "currency", column = @Column(name = "currency"))
    })
    private Money unitPrice;

    public static TicketOrderItem create(TicketOrder ticketOrder, TicketPool ticketPool, int quantity, Money unitPrice) {
        TicketOrderItem item = new TicketOrderItem();
        item.ticketOrder = ticketOrder;
        item.ticketPool = ticketPool;
        item.quantity = quantity;
        item.unitPrice = unitPrice;
        return item;
    }

    public void update(TicketOrder ticketOrder, TicketPool ticketPool, int quantity, Money unitPrice) {
        this.ticketOrder = ticketOrder;
        this.ticketPool = ticketPool;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }
}
