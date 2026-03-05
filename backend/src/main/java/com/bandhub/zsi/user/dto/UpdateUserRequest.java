package com.bandhub.zsi.user.dto;

public record UpdateUserRequest(
        String firstName,
        String lastName,
        String email
) {}
