package com.bandhub.zsi.ticketing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateTicketValidationRequest(
        @NotNull UUID ticketCodeId,
        String validatedBy,
        String gateName,
        @NotBlank String validationResult,
        String details
) {}
