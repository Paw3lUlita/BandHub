package com.bandhub.zsi.ticketing.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record TicketPoolResponse(
        UUID id,
        String name,
        BigDecimal price,
        String currency,
        int totalQuantity,
        int remainingQuantity
) {}
