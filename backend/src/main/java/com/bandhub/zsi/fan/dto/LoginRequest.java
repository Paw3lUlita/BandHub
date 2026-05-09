package com.bandhub.zsi.fan.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Nazwa uzytkownika jest wymagana")
        String username,

        @NotBlank(message = "Haslo jest wymagane")
        String password
) {}
