package com.bandhub.zsi.ticketing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateTicketOrderItemRequest(
        @NotNull UUID ticketOrderId,
        @NotNull UUID ticketPoolId,
        @Positive int quantity,
        @NotNull @PositiveOrZero BigDecimal unitPrice,
        @NotBlank String currency
) {}
