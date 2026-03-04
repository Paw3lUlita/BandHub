package com.bandhub.zsi.fan.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateFanNotificationRequest(
        @NotNull UUID id,
        String fanId,
        boolean broadcast,
        @NotBlank String title,
        @NotBlank String message,
        String module
) {}
