package com.bandhub.zsi.logistics.dto;

import java.util.UUID;

public record TourCategoryResponse(
        UUID id,
        String code,
        String name,
        boolean active
) {}
