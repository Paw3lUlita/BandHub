package com.bandhub.zsi.ticketing.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record TicketValidationResponse(
        UUID id,
        UUID ticketCodeId,
        String ticketCode,
        String username,
        String validatedBy,
        String gateName,
        String validationResult,
        LocalDateTime validationTime,
        String details
) {}
