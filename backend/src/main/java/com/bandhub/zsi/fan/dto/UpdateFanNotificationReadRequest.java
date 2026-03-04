package com.bandhub.zsi.fan.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record UpdateFanNotificationReadRequest(
        @NotNull UUID notificationId,
        @NotBlank String fanId,
        LocalDateTime readAt
) {}
