package com.bandhub.zsi.cms;

import com.bandhub.zsi.cms.dto.SiteSettingsResponse;
import com.bandhub.zsi.cms.dto.UpdateSiteSettingsRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/site-settings")
@PreAuthorize("hasRole('ADMIN')")
class SiteSettingsAdminController {

    private final SiteSettingsAdminService service;

    SiteSettingsAdminController(SiteSettingsAdminService service) {
        this.service = service;
    }

    @GetMapping
    ResponseEntity<SiteSettingsResponse> get() {
        return ResponseEntity.ok(service.getSettings());
    }

    @PutMapping
    ResponseEntity<SiteSettingsResponse> update(
            @RequestBody @Valid UpdateSiteSettingsRequest request,
            Authentication authentication
    ) {
        String updatedBy = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(service.updateSettings(request, updatedBy));
    }
}
