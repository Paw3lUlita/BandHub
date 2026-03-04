package com.bandhub.zsi.reporting.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record UpdateReportRunRequest(
        @NotBlank String reportName,
        String requestedBy,
        String parametersJson,
        @NotBlank String status,
        String fileFormat,
        LocalDateTime completedAt
) {}
