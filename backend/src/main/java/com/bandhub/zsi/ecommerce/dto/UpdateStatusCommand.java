package com.bandhub.zsi.ecommerce.dto;

import com.bandhub.zsi.ecommerce.domain.OrderStatus;

public record UpdateStatusCommand(OrderStatus newStatus) {}
