package com.bandhub.zsi.ticketing.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TicketOrderResponse(
        UUID id,
        String userId,
        UUID concertId,
        String status,
        BigDecimal totalAmount,
        String currency,
        LocalDateTime createdAt
) {}
