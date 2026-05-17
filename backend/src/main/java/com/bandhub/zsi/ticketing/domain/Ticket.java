package com.bandhub.zsi.ticketing.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Issued ticket row in {@code tickets} (fan-facing barcode / QR identity).
 * Linked to {@link TicketPool}, optional {@link TicketOrder}, and mirrored in {@link TicketCode}.
 */
@Entity
@Table(name = "tickets")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ticket {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "ticket_code", nullable = false, unique = true)
    private String ticketCode;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pool_id")
    private TicketPool ticketPool;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "purchase_date")
    private LocalDateTime purchaseDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_order_id")
    private TicketOrder ticketOrder;

    public static Ticket issue(String ticketCode, TicketPool pool, String userId, TicketOrder order) {
        Ticket ticket = new Ticket();
        ticket.ticketCode = ticketCode;
        ticket.ticketPool = pool;
        ticket.userId = userId;
        ticket.purchaseDate = LocalDateTime.now();
        ticket.ticketOrder = order;
        return ticket;
    }
}
