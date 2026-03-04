package com.bandhub.zsi.logistics.dto;

import java.math.BigDecimal;

public record TourProfitabilityResponse(
        BigDecimal totalCosts,
        BigDecimal ticketRevenue,
        BigDecimal manualRevenue,
        BigDecimal totalRevenue,
        BigDecimal balance,
        String currency
) {}
