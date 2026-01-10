package com.bandhub.zsi.ecommerce.dto;

import com.bandhub.zsi.ecommerce.domain.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record OrderSummaryResponse(
        UUID id,
        LocalDateTime createdAt,
        OrderStatus status,
        BigDecimal totalAmount,
        String currency,
        String userId // todo: zamienić z czasem na user email z keycloack
) {}