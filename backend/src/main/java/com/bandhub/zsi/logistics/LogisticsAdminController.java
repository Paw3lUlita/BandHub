package com.bandhub.zsi.logistics;

import com.bandhub.zsi.logistics.domain.Tour;
import com.bandhub.zsi.logistics.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/logistics")
@PreAuthorize("hasRole('ADMIN')")
class LogisticsAdminController {

    private final LogisticsAdminService service;

    LogisticsAdminController(LogisticsAdminService service) {
        this.service = service;
    }

    @GetMapping("/tours")
    ResponseEntity<List<TourResponse>> getAll() {
        var tours = service.getAllTours().stream()
                .map(t -> new TourResponse(t.getId(), t.getName(), t.getStartDate(), t.getEndDate()))
                .toList();
        return ResponseEntity.ok(tours);
    }

    @GetMapping("/tours/{id}")
    ResponseEntity<TourDetailResponse> getOne(@PathVariable UUID id) {
        Tour tour = service.getTour(id);

        // Mapujemy koszty
        var costResponses = tour.getCosts().stream()
                .map(c -> new TourCostResponse(c.getId(), c.getTitle(), c.getCost().amount(), c.getCost().currency(), c.getCostDate()))
                .toList();

        var response = new TourDetailResponse(
                tour.getId(), tour.getName(), tour.getDescription(),
                tour.getStartDate(), tour.getEndDate(), costResponses
        );
        return ResponseEntity.ok(response);
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
