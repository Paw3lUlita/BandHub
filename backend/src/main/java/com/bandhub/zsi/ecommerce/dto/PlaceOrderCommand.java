package com.bandhub.zsi.ecommerce.dto;

import java.util.Map;
import java.util.UUID;

public record PlaceOrderCommand(Map<UUID, Integer> items) {}
