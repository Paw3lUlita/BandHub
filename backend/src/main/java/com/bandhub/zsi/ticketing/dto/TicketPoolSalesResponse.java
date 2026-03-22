package com.bandhub.zsi.ticketing.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record TicketPoolSalesResponse(
        UUID poolId,
        String poolName,
        long sold,
        int remaining,
        int total,
        BigDecimal revenue,
        String currency
) {
}
