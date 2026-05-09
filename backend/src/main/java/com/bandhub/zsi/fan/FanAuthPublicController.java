package com.bandhub.zsi.fan;

import com.bandhub.zsi.fan.dto.LoginRequest;
import com.bandhub.zsi.fan.dto.LoginResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Public proxy do Keycloak token endpointu (password grant).
 * Ekran logowania w mobilce strzela tutaj zamiast bezposrednio do Keycloaka,
 * dzieki czemu uniknelismy konfiguracji CORS / Web Origins na realmie i mamy
 * jeden punkt wejscia dla mobile klienta.
 */
@RestController
@RequestMapping("/api/public/auth")
class FanAuthPublicController {

    private final FanAuthService service;

    FanAuthPublicController(FanAuthService service) {
        this.service = service;
    }

    @PostMapping("/login")
    ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(service.login(request));
    }
}
