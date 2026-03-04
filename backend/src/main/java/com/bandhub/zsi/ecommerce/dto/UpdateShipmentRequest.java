package com.bandhub.zsi.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record UpdateShipmentRequest(
        String carrier,
        String trackingNumber,
        @NotBlank String status,
        LocalDateTime shippedAt,
        LocalDateTime deliveredAt
) {}
