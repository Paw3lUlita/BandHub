package com.bandhub.zsi.fan.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record SetlistResponse(
        UUID id,
        UUID concertId,
        String concertName,
        String title,
        String createdBy,
        LocalDateTime publishedAt,
        LocalDateTime createdAt
) {}
