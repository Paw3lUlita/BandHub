package com.bandhub.zsi.ticketing.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ConcertTicketingSummaryResponse(
        UUID concertId,
        String concertName,
        int venueCapacity,
        long totalSold,
        BigDecimal totalRevenue,
        String currency,
        List<TicketPoolSalesResponse> pools
) {
}
