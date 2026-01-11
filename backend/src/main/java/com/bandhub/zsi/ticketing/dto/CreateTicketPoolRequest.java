package com.bandhub.zsi.ticketing.dto;

import java.math.BigDecimal;

public record CreateTicketPoolRequest(
        String name,
        BigDecimal price,
        String currency,
        int totalQuantity
) {}
