package com.bandhub.zsi.logistics.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record UpdateTourSettlementRequest(
        @NotNull UUID tourId,
        String settledBy,
        LocalDateTime settledAt,
        BigDecimal totalCosts,
        BigDecimal totalRevenue,
        BigDecimal balance,
        String currency,
        String notes
) {}
