package com.bandhub.zsi.fan.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "fan_notifications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FanNotification {

    @Id
    private UUID id;

    @Column(name = "fan_id")
    private String fanId;

    @Column(name = "is_broadcast")
    private boolean broadcast;

    private String title;
    private String message;
    private String module;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public static FanNotification create(UUID id, String fanId, boolean broadcast, String title, String message, String module) {
        FanNotification notification = new FanNotification();
        notification.id = id;
        notification.fanId = fanId;
        notification.broadcast = broadcast;
        notification.title = title;
        notification.message = message;
        notification.module = module;
        notification.createdAt = LocalDateTime.now();
        return notification;
    }

    public void update(String fanId, boolean broadcast, String title, String message, String module) {
        this.fanId = fanId;
        this.broadcast = broadcast;
        this.title = title;
        this.message = message;
        this.module = module;
    }
}
