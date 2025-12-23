package com.bandhub.zsi.ecommerce.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String name,
        String description,
        BigDecimal price,
        String currency,
        int stockQuantity,
        String categoryName
) {}
