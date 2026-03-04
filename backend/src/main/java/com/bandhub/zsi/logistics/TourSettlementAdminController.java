package com.bandhub.zsi.logistics;

import com.bandhub.zsi.logistics.dto.CreateTourSettlementRequest;
import com.bandhub.zsi.logistics.dto.TourSettlementResponse;
import com.bandhub.zsi.logistics.dto.UpdateTourSettlementRequest;
import com.bandhub.zsi.shared.api.PageResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/tour-settlements")
@PreAuthorize("hasRole('ADMIN')")
class TourSettlementAdminController {
    private final TourSettlementAdminService service;
    TourSettlementAdminController(TourSettlementAdminService service) { this.service = service; }
    @GetMapping ResponseEntity<List<TourSettlementResponse>> getAll() { return ResponseEntity.ok(service.getAll()); }
    @GetMapping("/page") ResponseEntity<PageResponse<TourSettlementResponse>> getPage(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "settledAt") String sortBy, @RequestParam(defaultValue = "desc") String sortDir, @RequestParam(defaultValue = "") String q) { return ResponseEntity.ok(service.getPage(page, size, sortBy, sortDir, q)); }
    @GetMapping("/{id}") ResponseEntity<TourSettlementResponse> getOne(@PathVariable UUID id) { return ResponseEntity.ok(service.getOne(id)); }
    @PostMapping ResponseEntity<Void> create(@RequestBody @Valid CreateTourSettlementRequest request) { UUID id = service.create(request); return ResponseEntity.created(URI.create("/api/admin/tour-settlements/" + id)).build(); }
    @PutMapping("/{id}") ResponseEntity<Void> update(@PathVariable UUID id, @RequestBody @Valid UpdateTourSettlementRequest request) { service.update(id, request); return ResponseEntity.noContent().build(); }
    @DeleteMapping("/{id}") ResponseEntity<Void> delete(@PathVariable UUID id) { service.delete(id); return ResponseEntity.noContent().build(); }
}
