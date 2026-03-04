package com.bandhub.zsi.ticketing.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record TicketCodeResponse(
        UUID id,
        UUID ticketId,
        String codeValue,
        String codeType,
        String status,
        LocalDateTime generatedAt,
        LocalDateTime activatedAt,
        LocalDateTime usedAt
) {}
