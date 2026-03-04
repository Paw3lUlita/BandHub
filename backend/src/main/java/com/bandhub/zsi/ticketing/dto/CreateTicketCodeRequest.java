package com.bandhub.zsi.ticketing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateTicketCodeRequest(
        @NotNull UUID ticketId,
        @NotBlank String codeValue,
        @NotBlank String codeType,
        @NotBlank String status
) {}
