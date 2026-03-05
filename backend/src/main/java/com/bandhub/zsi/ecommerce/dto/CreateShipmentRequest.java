package com.bandhub.zsi.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateShipmentRequest(
        @NotNull UUID id,
        @NotNull UUID orderId,
        String carrier,
        String trackingNumber,
        @NotBlank String status,
        LocalDateTime shippedAt,
        LocalDateTime deliveredAt,
        String deliveryAddress
) {}
