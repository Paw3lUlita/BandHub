package com.bandhub.zsi.ticketing.domain;

import com.bandhub.zsi.shared.Money;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ticket_refunds")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TicketRefund {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_order_id")
    private TicketOrder ticketOrder;

    @Column(name = "ticket_id")
    private UUID ticketId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "amount")),
            @AttributeOverride(name = "currency", column = @Column(name = "currency"))
    })
    private Money amount;

    private String reason;
    private String status;

    @Column(name = "requested_at")
    private LocalDateTime requestedAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    public static TicketRefund create(TicketOrder ticketOrder, UUID ticketId, Money amount, String reason, String status, LocalDateTime resolvedAt) {
        TicketRefund refund = new TicketRefund();
        refund.ticketOrder = ticketOrder;
        refund.ticketId = ticketId;
        refund.amount = amount;
        refund.reason = reason;
        refund.status = status;
        refund.requestedAt = LocalDateTime.now();
        refund.resolvedAt = resolvedAt;
        return refund;
    }

    public void update(TicketOrder ticketOrder, UUID ticketId, Money amount, String reason, String status, LocalDateTime resolvedAt) {
        this.ticketOrder = ticketOrder;
        this.ticketId = ticketId;
        this.amount = amount;
        this.reason = reason;
        this.status = status;
        this.resolvedAt = resolvedAt;
    }
}
