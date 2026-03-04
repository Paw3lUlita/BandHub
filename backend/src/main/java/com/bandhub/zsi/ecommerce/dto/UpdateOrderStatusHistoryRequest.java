package com.bandhub.zsi.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateOrderStatusHistoryRequest(
        String oldStatus,
        @NotBlank String newStatus,
        String changedBy,
        String note
) {}
