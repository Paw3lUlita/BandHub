package com.bandhub.zsi.logistics;

import com.bandhub.zsi.logistics.dto.*; // Import DTO
import com.bandhub.zsi.shared.api.PageResponse;
import com.bandhub.zsi.shared.security.AuthenticationDisplayName;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/logistics")
@PreAuthorize("hasRole('ADMIN')")
class LogisticsAdminController {

    private final LogisticsAdminService service;
    private final TourSettlementAdminService settlementAdminService;

    LogisticsAdminController(LogisticsAdminService service, TourSettlementAdminService settlementAdminService) {
        this.service = service;
        this.settlementAdminService = settlementAdminService;
    }

    @PostMapping("/tours")
    ResponseEntity<Void> createTour(@RequestBody @Valid CreateTourRequest request) {
        UUID id = service.createTour(request);
        return ResponseEntity.created(URI.create("/api/admin/logistics/tours/" + id)).build();
    }

    @PutMapping("/tours/{id}")
    ResponseEntity<Void> updateTour(@PathVariable UUID id, @RequestBody @Valid UpdateTourRequest request) {
        service.updateTour(id, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/tours/{id}")
    ResponseEntity<Void> deleteTour(@PathVariable UUID id) {
        service.deleteTour(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/tours/{tourId}/costs")
    ResponseEntity<Void> addCost(@PathVariable UUID tourId, @RequestBody @Valid CreateCostRequest request) {
        service.addCost(tourId, request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/tours/{tourId}/costs/{costId}")
    ResponseEntity<Void> updateCost(@PathVariable UUID tourId, @PathVariable UUID costId, @RequestBody @Valid UpdateCostRequest request) {
        service.updateCost(tourId, costId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/tours/{tourId}/costs/{costId}")
    ResponseEntity<Void> deleteCost(@PathVariable UUID tourId, @PathVariable UUID costId) {
        service.deleteCost(tourId, costId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/tours/{tourId}/revenues")
    ResponseEntity<Void> addRevenue(@PathVariable UUID tourId, @RequestBody @Valid CreateRevenueRequest request) {
        service.addRevenue(tourId, request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/tours/{tourId}/revenues/{revenueId}")
    ResponseEntity<Void> updateRevenue(@PathVariable UUID tourId, @PathVariable UUID revenueId, @RequestBody @Valid UpdateRevenueRequest request) {
        service.updateRevenue(tourId, revenueId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/tours/{tourId}/revenues/{revenueId}")
    ResponseEntity<Void> deleteRevenue(@PathVariable UUID tourId, @PathVariable UUID revenueId) {
        service.deleteRevenue(tourId, revenueId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/tours")
    ResponseEntity<List<TourResponse>> getAll() {
        return ResponseEntity.ok(service.getAllTours());
    }

    @GetMapping("/tours/page")
    ResponseEntity<PageResponse<TourResponse>> getPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(defaultValue = "") String q
    ) {
        return ResponseEntity.ok(service.getToursPage(page, size, sortBy, sortDir, q));
    }

    @GetMapping("/tours/{id}")
    ResponseEntity<TourDetailResponse> getOne(@PathVariable UUID id) {
        // Serwis zwraca gotowe DTO, więc LazyInitializationException nie wystąpi
        return ResponseEntity.ok(service.getTourDetails(id));
    }

    @GetMapping("/tours/{id}/profitability")
    ResponseEntity<TourProfitabilityResponse> getProfitability(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getProfitability(id));
    }

    @GetMapping("/tours/{tourId}/settlement")
    ResponseEntity<TourSettlementResponse> getSettlement(@PathVariable UUID tourId) {
        return settlementAdminService.findByTourId(tourId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/tours/{tourId}/settlement/close")
    ResponseEntity<TourSettlementResponse> closeSettlement(
            @PathVariable UUID tourId,
            @RequestBody(required = false) CloseTourSettlementRequest body,
            Authentication authentication
    ) {
        String actor = AuthenticationDisplayName.resolve(authentication);
        String notes = body != null ? body.notes() : null;
        return ResponseEntity.ok(settlementAdminService.closeFromComputedData(tourId, actor, notes));
    }
}