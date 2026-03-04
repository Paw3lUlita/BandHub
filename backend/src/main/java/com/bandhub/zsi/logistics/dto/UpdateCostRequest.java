package com.bandhub.zsi.logistics.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record UpdateCostRequest(
        @NotBlank(message = "Cost title is required")
        String title,

        @NotNull(message = "Cost amount is required")
        @Positive(message = "Cost amount must be greater than zero")
        BigDecimal amount,

        @NotBlank(message = "Currency is required")
        @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must use ISO 4217 format, e.g. PLN")
        String currency,

        @NotNull(message = "Cost date is required")
        LocalDateTime date
) {}
