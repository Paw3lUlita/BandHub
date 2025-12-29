package com.bandhub.zsi.ecommerce.dto;

import java.util.UUID;

public record UpdateCategoryCommand(UUID id, String name) {}
