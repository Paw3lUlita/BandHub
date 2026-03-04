package com.bandhub.zsi.ticketing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record UpdateTicketValidationRequest(
        @NotNull UUID ticketCodeId,
        String validatedBy,
        String gateName,
        @NotBlank String validationResult,
        LocalDateTime validationTime,
        String details
) {}
