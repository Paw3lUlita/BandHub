package com.bandhub.zsi.logistics.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreateTourLegRequest(
        @NotNull UUID tourId,
        UUID concertId,
        int legOrder,
        @NotBlank String city,
        String venueName,
        LocalDateTime legDate,
        BigDecimal plannedBudget,
        String currency
) {}
