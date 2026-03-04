package com.bandhub.zsi.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateOrderStatusHistoryRequest(
        @NotNull UUID orderId,
        String oldStatus,
        @NotBlank String newStatus,
        String changedBy,
        String note
) {}
