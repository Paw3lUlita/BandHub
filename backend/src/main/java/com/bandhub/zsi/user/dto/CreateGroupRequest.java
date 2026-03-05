package com.bandhub.zsi.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateGroupRequest(
        @NotBlank(message = "Nazwa grupy jest wymagana")
        @Size(min = 1, max = 255)
        String name,
        String path
) {}
