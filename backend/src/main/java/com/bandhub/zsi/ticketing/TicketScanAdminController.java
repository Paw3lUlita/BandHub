package com.bandhub.zsi.ticketing;

import com.bandhub.zsi.ticketing.dto.ScanTicketRequest;
import com.bandhub.zsi.ticketing.dto.ScanTicketResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/ticketing")
@PreAuthorize("hasRole('ADMIN')")
class TicketScanAdminController {

    private final TicketScanAdminService service;

    TicketScanAdminController(TicketScanAdminService service) {
        this.service = service;
    }

    @PostMapping("/scan")
    ResponseEntity<ScanTicketResponse> scan(@RequestBody @Valid ScanTicketRequest request, Authentication authentication) {
        String by = authentication != null ? authentication.getName() : "system";
        return ResponseEntity.ok(service.scan(request, by));
    }
}
