package com.bandhub.zsi.ticketing.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record UpdateTicketCodeRequest(
        @NotBlank String codeValue,
        @NotBlank String codeType,
        @NotBlank String status,
        LocalDateTime activatedAt,
        LocalDateTime usedAt
) {}
