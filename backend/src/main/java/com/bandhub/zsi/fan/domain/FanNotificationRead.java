package com.bandhub.zsi.fan.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "fan_notification_reads")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FanNotificationRead {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_id", nullable = false)
    private FanNotification notification;

    @Column(name = "fan_id")
    private String fanId;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    public static FanNotificationRead create(FanNotification notification, String fanId, LocalDateTime readAt) {
        FanNotificationRead notificationRead = new FanNotificationRead();
        notificationRead.notification = notification;
        notificationRead.fanId = fanId;
        notificationRead.readAt = readAt == null ? LocalDateTime.now() : readAt;
        return notificationRead;
    }

    public void update(FanNotification notification, String fanId, LocalDateTime readAt) {
        this.notification = notification;
        this.fanId = fanId;
        this.readAt = readAt;
    }
}
