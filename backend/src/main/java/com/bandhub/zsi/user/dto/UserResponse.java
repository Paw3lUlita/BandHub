package com.bandhub.zsi.user.dto;

import java.util.List;

public record UserResponse(
        String id,
        String username,
        String firstName,
        String lastName,
        String email,
        boolean enabled,
        long createdTimestamp,
        List<String> roles
) {}
