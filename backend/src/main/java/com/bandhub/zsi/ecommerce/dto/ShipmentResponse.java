package com.bandhub.zsi.ecommerce.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ShipmentResponse(
        UUID id,
        UUID orderId,
        String carrier,
        String trackingNumber,
        String status,
        LocalDateTime shippedAt,
        LocalDateTime deliveredAt,
        LocalDateTime createdAt,
        String deliveryAddress
) {}
