package com.bandhub.zsi.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateRoleRequest(
        @NotBlank(message = "Nazwa roli jest wymagana")
        @Size(min = 1, max = 255)
        String name,
        String description
) {}
