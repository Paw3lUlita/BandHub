package com.bandhub.zsi.ecommerce.dto;

import java.math.BigDecimal;

public record MerchSalesSnapshotResponse(
        long orderCount,
        BigDecimal totalRevenue,
        String currency,
        long totalUnits
) {}
