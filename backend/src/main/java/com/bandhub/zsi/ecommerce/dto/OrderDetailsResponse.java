package com.bandhub.zsi.ecommerce.dto;

import com.bandhub.zsi.ecommerce.domain.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderDetailsResponse(
        UUID id,
        LocalDateTime createdAt,
        OrderStatus status,
        BigDecimal totalAmount,
        String currency,
        String userId,
        List<OrderItemDto> items
) {}