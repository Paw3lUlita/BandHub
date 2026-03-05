package com.bandhub.zsi.user.dto;

public record RoleResponse(
        String id,
        String name,
        String description,
        boolean composite
) {}
