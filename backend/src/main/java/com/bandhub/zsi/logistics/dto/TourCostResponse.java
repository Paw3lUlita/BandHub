package com.bandhub.zsi.logistics.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TourCostResponse(
        UUID id,
        String title,
        BigDecimal amount,
        String currency,
        LocalDateTime date
) {}
