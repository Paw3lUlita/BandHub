package com.bandhub.zsi.logistics.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record TourResponse(
        UUID id,
        String name,
        LocalDateTime startDate,
        LocalDateTime endDate
) {}
