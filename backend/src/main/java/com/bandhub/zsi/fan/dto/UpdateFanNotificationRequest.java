package com.bandhub.zsi.fan.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateFanNotificationRequest(
        String fanId,
        boolean broadcast,
        @NotBlank String title,
        @NotBlank String message,
        String module
) {}
