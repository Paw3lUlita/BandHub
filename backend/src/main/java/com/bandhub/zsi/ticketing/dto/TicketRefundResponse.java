package com.bandhub.zsi.ticketing.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TicketRefundResponse(
        UUID id,
        UUID ticketOrderId,
        UUID ticketId,
        BigDecimal amount,
        String currency,
        String reason,
        String status,
        LocalDateTime requestedAt,
        LocalDateTime resolvedAt
) {}
