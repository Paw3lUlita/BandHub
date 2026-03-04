package com.bandhub.zsi.ecommerce.dto;

import com.bandhub.zsi.ecommerce.domain.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateStatusCommand(
        @NotNull(message = "New status is required")
        OrderStatus newStatus
) {}
