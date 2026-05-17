package com.bandhub.zsi.ticketing.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record MyTicketOrderResponse(
        UUID orderId,
        UUID concertId,
        String concertName,
        LocalDateTime concertDate,
        String status,
        BigDecimal totalAmount,
        String currency,
        LocalDateTime createdAt,
        List<String> ticketCodes
) {}
