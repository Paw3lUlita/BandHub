package com.bandhub.zsi.ticketing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateTicketOrderRequest(
        @NotBlank String userId,
        @NotNull UUID concertId,
        @NotBlank String status,
        @NotNull @PositiveOrZero BigDecimal totalAmount,
        @NotBlank String currency
) {}
