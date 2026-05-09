package com.bandhub.zsi.cms.dto;

import java.time.LocalDateTime;

public record SiteSettingsResponse(
        String bandName,
        String tagline,
        String heroImageUrl,
        String aboutText,
        LocalDateTime updatedAt,
        String updatedBy
) {}
