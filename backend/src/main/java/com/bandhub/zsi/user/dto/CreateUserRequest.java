package com.bandhub.zsi.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotBlank(message = "Username jest wymagany")
        @Size(min = 1, max = 255)
        String username,
        @NotBlank(message = "Hasło jest wymagane")
        @Size(min = 8, message = "Hasło musi mieć minimum 8 znaków")
        String password,
        String firstName,
        String lastName,
        String email,
        boolean enabled
) {}
