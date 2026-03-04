package com.bandhub.zsi.reporting.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ExportJobResponse(
        UUID id,
        String module,
        String entityName,
        String status,
        String requestedBy,
        String filePath,
        LocalDateTime createdAt,
        LocalDateTime completedAt
) {}
