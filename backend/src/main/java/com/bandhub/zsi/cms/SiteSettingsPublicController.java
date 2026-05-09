package com.bandhub.zsi.cms;

import com.bandhub.zsi.cms.dto.SiteSettingsResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Publiczny feed brandingu i tresci edytowanych z panelu admina.
 * Mobilka zaciaga te dane przy starcie - dzieki temu zaden tekst/grafika
 * branzingowa nie jest hardcodowana w kliencie (wymog projektowy).
 */
@RestController
@RequestMapping("/api/public/site-settings")
class SiteSettingsPublicController {

    private final SiteSettingsAdminService service;

    SiteSettingsPublicController(SiteSettingsAdminService service) {
        this.service = service;
    }

    @GetMapping
    ResponseEntity<SiteSettingsResponse> get() {
        return ResponseEntity.ok(service.getSettings());
    }
}
