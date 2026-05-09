package com.bandhub.zsi.reporting.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record DocxTemplateResponse(
        UUID id,
        String name,
        String moduleCode,
        int templateVersion,
        boolean active,
        String filePath,
        LocalDateTime createdAt
) {
}
