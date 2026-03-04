package com.bandhub.zsi.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdatePaymentTransactionRequest(
        @NotBlank String eventType,
        String externalStatus,
        String payload
) {}
