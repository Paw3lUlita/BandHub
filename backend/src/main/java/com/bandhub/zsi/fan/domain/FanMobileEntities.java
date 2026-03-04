package com.bandhub.zsi.fan.domain;

import com.bandhub.zsi.ticketing.domain.Concert;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "setlists")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class Setlist {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id", nullable = false)
    private Concert concert;

    private String title;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}

@Entity
@Table(name = "setlist_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class SetlistItem {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "setlist_id", nullable = false)
    private Setlist setlist;

    @Column(name = "song_title")
    private String songTitle;

    @Column(name = "song_order")
    private int songOrder;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;
}

@Entity
@Table(name = "fan_favorites")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class FanFavorite {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "fan_id")
    private String fanId;

    @Column(name = "favorite_type")
    private String favoriteType;

    @Column(name = "reference_id")
    private UUID referenceId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}

@Entity
@Table(name = "fan_notifications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class FanNotification {

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
}

@Entity
@Table(name = "fan_notification_reads")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class FanNotificationRead {

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
}

@Entity
@Table(name = "fan_devices")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class FanDevice {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "fan_id")
    private String fanId;

    @Column(name = "device_token")
    private String deviceToken;

    private String platform;

    @Column(name = "app_version")
    private String appVersion;

    @Column(name = "last_seen_at")
    private LocalDateTime lastSeenAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
