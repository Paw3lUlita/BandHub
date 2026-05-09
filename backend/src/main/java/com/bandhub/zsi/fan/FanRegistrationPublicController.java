package com.bandhub.zsi.fan;

import com.bandhub.zsi.fan.dto.FanRegistrationRequest;
import com.bandhub.zsi.fan.dto.FanRegistrationResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Publiczna rejestracja fana (samoobsługa). Tworzy uzytkownika w Keycloaku z rola FAN.
 * Endpoint pod permitAll w SecurityConfig (`/api/public/**`).
 */
@RestController
@RequestMapping("/api/public/register")
class FanRegistrationPublicController {

    private final FanRegistrationService service;

    FanRegistrationPublicController(FanRegistrationService service) {
        this.service = service;
    }

    @PostMapping
    ResponseEntity<FanRegistrationResponse> register(@RequestBody @Valid FanRegistrationRequest request) {
        FanRegistrationResponse response = service.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
