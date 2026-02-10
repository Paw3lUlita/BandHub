package com.bandhub.zsi.logistics.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateCostRequest(
        String title,
        BigDecimal amount,
        String currency,
        LocalDateTime date
) {}
