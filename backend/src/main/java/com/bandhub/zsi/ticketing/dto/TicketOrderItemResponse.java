package com.bandhub.zsi.ticketing.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record TicketOrderItemResponse(
        UUID id,
        UUID ticketOrderId,
        UUID ticketPoolId,
        String ticketPoolName,
        String concertName,
        int quantity,
        BigDecimal unitPrice,
        String currency
) {}
