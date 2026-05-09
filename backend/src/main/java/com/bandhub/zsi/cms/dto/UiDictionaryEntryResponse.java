package com.bandhub.zsi.cms.dto;

import java.time.LocalDateTime;

public record UiDictionaryEntryResponse(
        String key,
        String value,
        String description,
        LocalDateTime updatedAt,
        String updatedBy
) {}
