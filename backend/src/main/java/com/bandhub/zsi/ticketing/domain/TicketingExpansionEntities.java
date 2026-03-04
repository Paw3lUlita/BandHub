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
class TicketOrder {

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
}

@Entity
@Table(name = "ticket_order_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class TicketOrderItem {

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
}

@Entity
@Table(name = "ticket_codes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class TicketCode {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "ticket_id")
    private UUID ticketId;

    @Column(name = "code_value")
    private String codeValue;

    @Column(name = "code_type")
    private String codeType;

    private String status;

    @Column(name = "generated_at")
    private LocalDateTime generatedAt;

    @Column(name = "activated_at")
    private LocalDateTime activatedAt;

    @Column(name = "used_at")
    private LocalDateTime usedAt;
}

@Entity
@Table(name = "ticket_validations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class TicketValidation {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_code_id", nullable = false)
    private TicketCode ticketCode;

    @Column(name = "validated_by")
    private String validatedBy;

    @Column(name = "gate_name")
    private String gateName;

    @Column(name = "validation_result")
    private String validationResult;

    @Column(name = "validation_time")
    private LocalDateTime validationTime;

    private String details;
}

@Entity
@Table(name = "ticket_refunds")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class TicketRefund {

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
}
