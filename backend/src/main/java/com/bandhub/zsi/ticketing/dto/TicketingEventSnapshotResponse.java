package com.bandhub.zsi.ticketing.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record TicketingEventSnapshotResponse(
        UUID concertId,
        String concertName,
        long soldTickets,
        long remainingTickets,
        BigDecimal totalRevenue,
        String currency,
        int venueCapacity,
        double occupancyPercent
) {
}
