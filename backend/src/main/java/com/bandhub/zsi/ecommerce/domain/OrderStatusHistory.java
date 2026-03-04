package com.bandhub.zsi.ecommerce.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "order_status_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderStatusHistory {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "old_status")
    private String oldStatus;

    @Column(name = "new_status")
    private String newStatus;

    @Column(name = "changed_by")
    private String changedBy;

    @Column(name = "changed_at")
    private LocalDateTime changedAt;

    private String note;

    public static OrderStatusHistory create(Order order, String oldStatus, String newStatus, String changedBy, String note) {
        OrderStatusHistory history = new OrderStatusHistory();
        history.order = order;
        history.oldStatus = oldStatus;
        history.newStatus = newStatus;
        history.changedBy = changedBy;
        history.changedAt = LocalDateTime.now();
        history.note = note;
        return history;
    }

    public void update(String oldStatus, String newStatus, String changedBy, String note) {
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.changedBy = changedBy;
        this.note = note;
    }
}
