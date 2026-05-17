package com.bandhub.zsi.fan.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record FanDeviceResponse(
        UUID id,
        String fanId,
        String fanUsername,
        String deviceToken,
        String platform,
        String appVersion,
        LocalDateTime lastSeenAt,
        LocalDateTime createdAt
) {}
