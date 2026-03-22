package com.bandhub.zsi.ticketing.dto;

import java.util.List;
import java.util.UUID;

public record TicketPurchaseResponse(
        UUID orderId,
        List<String> ticketCodes
) {
}
