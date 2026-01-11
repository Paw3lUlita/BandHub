package com.bandhub.zsi.ticketing;

import com.bandhub.zsi.ticketing.dto.CreateVenueRequest;
import com.bandhub.zsi.ticketing.dto.VenueResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/venues")
@PreAuthorize("hasRole('ADMIN')")
class VenueAdminController {

    private final VenueAdminService service;

    VenueAdminController(VenueAdminService service) {
        this.service = service;
    }

    @GetMapping
    ResponseEntity<List<VenueResponse>> getAll() {
        return ResponseEntity.ok(service.getAllVenues());
    }

    @PostMapping
    ResponseEntity<Void> create(@RequestBody CreateVenueRequest request) {
        UUID id = service.createVenue(request);
        return ResponseEntity.created(URI.create("/api/admin/venues/" + id)).build();
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.deleteVenue(id);
        return ResponseEntity.noContent().build();
    }
}
