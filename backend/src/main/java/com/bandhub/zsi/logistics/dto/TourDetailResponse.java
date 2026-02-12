package com.bandhub.zsi.logistics.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record TourDetailResponse(
        UUID id,
        String name,
        String description,
        LocalDateTime startDate,
        LocalDateTime endDate,
        List<TourCostResponse> costs
) {}
