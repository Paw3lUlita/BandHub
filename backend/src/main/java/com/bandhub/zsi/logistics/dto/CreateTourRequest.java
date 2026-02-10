package com.bandhub.zsi.logistics.dto;

import java.time.LocalDateTime;

public record CreateTourRequest(
        String name,
        String description,
        LocalDateTime startDate,
        LocalDateTime endDate
) {}