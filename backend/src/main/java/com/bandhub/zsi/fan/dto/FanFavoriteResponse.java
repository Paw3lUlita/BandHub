package com.bandhub.zsi.fan.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record FanFavoriteResponse(
        UUID id,
        String fanId,
        String favoriteType,
        UUID referenceId,
        LocalDateTime createdAt
) {}
