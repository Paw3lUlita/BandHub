package com.bandhub.zsi.ecommerce.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentResponse(
        UUID id,
        UUID orderId,
        String provider,
        String providerPaymentId,
        String status,
        BigDecimal amount,
        String currency,
        LocalDateTime paidAt,
        LocalDateTime createdAt
) {}
