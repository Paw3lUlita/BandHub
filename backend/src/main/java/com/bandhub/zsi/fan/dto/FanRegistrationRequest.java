package com.bandhub.zsi.fan.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record FanRegistrationRequest(
        @NotBlank(message = "Nazwa uzytkownika jest wymagana")
        @Size(min = 3, max = 64, message = "Nazwa uzytkownika musi miec 3-64 znaki")
        // Regex zgodny z domyslnym User Profile w Keycloak (litery, cyfry, kropka,
        // podkreslnik, myslnik, malpa). Brak spacji - inaczej Keycloak rzuca
        // error-username-invalid-character i mamy nieczytelny 400 z realmu.
        @Pattern(
                regexp = "^[A-Za-z0-9._@-]+$",
                message = "Nazwa uzytkownika moze zawierac tylko litery, cyfry oraz znaki . _ - @ (bez spacji)"
        )
        String username,

        @NotBlank(message = "Haslo jest wymagane")
        @Size(min = 8, message = "Haslo musi miec minimum 8 znakow")
        String password,

        @Email(message = "Niepoprawny email")
        String email,

        String firstName,

        String lastName
) {}
