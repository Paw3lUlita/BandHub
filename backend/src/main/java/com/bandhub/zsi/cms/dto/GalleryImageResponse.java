package com.bandhub.zsi.cms.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record GalleryImageResponse(
        UUID id,
        String title,
        String imageUrl,
        LocalDateTime uploadedAt
) {}