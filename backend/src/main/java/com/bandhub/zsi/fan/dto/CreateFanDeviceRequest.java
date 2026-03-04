package com.bandhub.zsi.fan.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record CreateFanDeviceRequest(
        @NotBlank String fanId,
        @NotBlank String deviceToken,
        @NotBlank String platform,
        String appVersion,
        LocalDateTime lastSeenAt
) {}
