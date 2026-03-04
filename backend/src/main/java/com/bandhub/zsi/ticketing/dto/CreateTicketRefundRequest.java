package com.bandhub.zsi.ticketing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreateTicketRefundRequest(
        UUID ticketOrderId,
        UUID ticketId,
        @NotNull @PositiveOrZero BigDecimal amount,
        @NotBlank String currency,
        String reason,
        @NotBlank String status,
        LocalDateTime resolvedAt
) {}
