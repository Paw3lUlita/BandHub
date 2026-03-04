package com.bandhub.zsi.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreatePaymentTransactionRequest(
        @NotNull UUID paymentId,
        @NotBlank String eventType,
        String externalStatus,
        String payload
) {}
