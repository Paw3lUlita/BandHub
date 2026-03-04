package com.bandhub.zsi.reporting.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record CreateExportJobRequest(
        @NotBlank String module,
        @NotBlank String entityName,
        @NotBlank String status,
        String requestedBy,
        String filePath,
        LocalDateTime completedAt
) {}
