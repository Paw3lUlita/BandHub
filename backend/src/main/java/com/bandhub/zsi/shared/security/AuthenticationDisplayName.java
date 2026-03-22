package com.bandhub.zsi.shared.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

/**
 * Etykieta do zapisu w polach audytu (np. {@code settled_by}): preferuje {@code preferred_username} / {@code name} z JWT zamiast {@code sub}.
 */
public final class AuthenticationDisplayName {

    private AuthenticationDisplayName() {
    }

    public static String resolve(Authentication authentication) {
        if (authentication == null) {
            return "system";
        }
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            String preferred = jwt.getClaimAsString("preferred_username");
            if (preferred != null && !preferred.isBlank()) {
                return preferred;
            }
            String name = jwt.getClaimAsString("name");
            if (name != null && !name.isBlank()) {
                return name;
            }
        }
        String name = authentication.getName();
        return name != null && !name.isBlank() ? name : "system";
    }
}
