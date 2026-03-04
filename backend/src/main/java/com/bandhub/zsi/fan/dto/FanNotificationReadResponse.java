package com.bandhub.zsi.fan.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record FanNotificationReadResponse(
        UUID id,
        UUID notificationId,
        String fanId,
        LocalDateTime readAt
) {}
