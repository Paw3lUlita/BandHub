package com.bandhub.zsi.fan.dto;

import java.util.UUID;

public record SetlistItemResponse(
        UUID id,
        UUID setlistId,
        String songTitle,
        int songOrder,
        Integer durationSeconds
) {}
