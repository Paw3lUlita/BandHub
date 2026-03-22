package com.bandhub.zsi.ticketing.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record AttendeeRowResponse(
        String ticketCode,
        String userId,
        UUID orderId,
        String poolName,
        LocalDateTime purchaseDate
) {
}
