package com.bandhub.zsi.reporting.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReportRunResponse(
        UUID id,
        String reportName,
        String requestedBy,
        String parametersJson,
        String status,
        String fileFormat,
        LocalDateTime createdAt,
        LocalDateTime completedAt
) {}
