package com.bandhub.zsi.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreatePaymentRequest(
        @NotNull UUID id,
        @NotNull UUID orderId,
        String provider,
        String providerPaymentId,
        @NotBlank String status,
        @NotNull @PositiveOrZero BigDecimal amount,
        @NotBlank String currency,
        LocalDateTime paidAt
) {}
