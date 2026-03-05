package com.bandhub.zsi.user.dto;

import jakarta.validation.constraints.NotBlank;

public record AssignRoleRequest(
        @NotBlank(message = "Nazwa roli jest wymagana")
        String roleName
) {}
