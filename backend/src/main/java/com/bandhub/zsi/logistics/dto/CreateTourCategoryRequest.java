package com.bandhub.zsi.logistics.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateTourCategoryRequest(
        @NotBlank String code,
        @NotBlank String name,
        boolean active
) {}
