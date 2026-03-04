package com.bandhub.zsi.logistics.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TourLegResponse(
        UUID id,
        UUID tourId,
        UUID concertId,
        int legOrder,
        String city,
        String venueName,
        LocalDateTime legDate,
        BigDecimal plannedBudget,
        String currency
) {}
