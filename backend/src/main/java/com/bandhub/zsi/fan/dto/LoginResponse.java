package com.bandhub.zsi.fan.dto;

/**
 * Odpowiedz na logowanie fana - mobilka trzyma `accessToken` w AsyncStorage
 * i dolacza go jako Bearer do kolejnych zapytan. `refreshToken` zachowany na wypadek
 * gdyby mobilka chciala odswiezac sesje (na razie polega na re-login po wygasnieciu).
 */
public record LoginResponse(
        String accessToken,
        String refreshToken,
        Integer expiresIn,
        String tokenType
) {}
