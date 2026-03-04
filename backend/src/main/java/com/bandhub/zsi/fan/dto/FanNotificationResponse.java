package com.bandhub.zsi.fan.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record FanNotificationResponse(
        UUID id,
        String fanId,
        boolean broadcast,
        String title,
        String message,
        String module,
        LocalDateTime createdAt
) {}
