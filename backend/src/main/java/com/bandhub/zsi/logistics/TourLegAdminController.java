package com.bandhub.zsi.logistics;

import com.bandhub.zsi.logistics.dto.CreateTourLegRequest;
import com.bandhub.zsi.logistics.dto.TourLegResponse;
import com.bandhub.zsi.logistics.dto.UpdateTourLegRequest;
import com.bandhub.zsi.shared.api.PageResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/tour-legs")
@PreAuthorize("hasRole('ADMIN')")
class TourLegAdminController {

    private final TourLegAdminService service;

    TourLegAdminController(TourLegAdminService service) { this.service = service; }

    @GetMapping ResponseEntity<List<TourLegResponse>> getAll() { return ResponseEntity.ok(service.getAll()); }

    @GetMapping("/page")
    ResponseEntity<PageResponse<TourLegResponse>> getPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "legOrder") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(defaultValue = "") String q
    ) { return ResponseEntity.ok(service.getPage(page, size, sortBy, sortDir, q)); }

    @GetMapping("/{id}") ResponseEntity<TourLegResponse> getOne(@PathVariable UUID id) { return ResponseEntity.ok(service.getOne(id)); }

    @PostMapping
    ResponseEntity<Void> create(@RequestBody @Valid CreateTourLegRequest request) {
        UUID id = service.create(request);
        return ResponseEntity.created(URI.create("/api/admin/tour-legs/" + id)).build();
    }

    @PutMapping("/{id}")
    ResponseEntity<Void> update(@PathVariable UUID id, @RequestBody @Valid UpdateTourLegRequest request) {
        service.update(id, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable UUID id) { service.delete(id); return ResponseEntity.noContent().build(); }
}
