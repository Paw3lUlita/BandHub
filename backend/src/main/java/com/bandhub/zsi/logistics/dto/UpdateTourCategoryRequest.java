package com.bandhub.zsi.logistics.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateTourCategoryRequest(
        @NotBlank String code,
        @NotBlank String name,
        boolean active
) {}
