package com.bandhub.zsi.logistics;

import com.bandhub.zsi.logistics.dto.CreateCostRequest;
import com.bandhub.zsi.logistics.dto.CreateTourRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/logistics")
@PreAuthorize("hasRole('ADMIN')")
class LogisticsAdminController {

    private final LogisticsAdminService service;

    LogisticsAdminController(LogisticsAdminService service) {
        this.service = service;
    }

    @PostMapping("/tours")
    ResponseEntity<Void> createTour(@RequestBody CreateTourRequest request) {
        UUID id = service.createTour(request);

        return ResponseEntity.created(URI.create("/api/admin/logistics/tours/" + id)).build();
    }

    @PostMapping("/tours/{tourId}/costs")
    ResponseEntity<Void> addCost(@PathVariable UUID tourId, @RequestBody CreateCostRequest request) {
        service.addCost(tourId, request);
        return ResponseEntity.ok().build();
    }
}
