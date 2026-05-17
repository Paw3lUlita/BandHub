package com.bandhub.zsi.ecommerce.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrderStatusHistoryResponse(
        UUID id,
        UUID orderId,
        String oldStatus,
        String newStatus,
        String changedBy,
        String changedByUsername,
        LocalDateTime changedAt,
        String note
) {}
