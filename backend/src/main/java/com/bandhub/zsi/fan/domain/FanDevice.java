package com.bandhub.zsi.fan.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "fan_devices")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FanDevice {

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

    public static FanDevice create(String fanId, String deviceToken, String platform, String appVersion, LocalDateTime lastSeenAt) {
        FanDevice device = new FanDevice();
        device.fanId = fanId;
        device.deviceToken = deviceToken;
        device.platform = platform;
        device.appVersion = appVersion;
        device.lastSeenAt = lastSeenAt;
        device.createdAt = LocalDateTime.now();
        return device;
    }

    public void update(String fanId, String deviceToken, String platform, String appVersion, LocalDateTime lastSeenAt) {
        this.fanId = fanId;
        this.deviceToken = deviceToken;
        this.platform = platform;
        this.appVersion = appVersion;
        this.lastSeenAt = lastSeenAt;
    }
}
