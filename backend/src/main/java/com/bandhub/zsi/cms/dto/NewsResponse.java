package com.bandhub.zsi.cms.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record NewsResponse(
        UUID id,
        String title,
        String content,
        String imageUrl,
        LocalDateTime publishedDate,
        String authorId
) {}
