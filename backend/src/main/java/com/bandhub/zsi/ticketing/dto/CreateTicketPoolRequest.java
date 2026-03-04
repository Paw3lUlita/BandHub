package com.bandhub.zsi.ticketing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateTicketPoolRequest(
        @NotBlank(message = "Ticket pool name is required")
        String name,

        @NotNull(message = "Ticket price is required")
        @Positive(message = "Ticket price must be greater than zero")
        BigDecimal price,

        @NotBlank(message = "Currency is required")
        @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must use ISO 4217 format, e.g. PLN")
        String currency,

        @Positive(message = "Total quantity must be greater than zero")
        int totalQuantity
) {}
