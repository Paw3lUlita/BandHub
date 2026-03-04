package com.bandhub.zsi.ecommerce.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentTransactionResponse(
        UUID id,
        UUID paymentId,
        String eventType,
        String externalStatus,
        String payload,
        LocalDateTime createdAt
) {}
