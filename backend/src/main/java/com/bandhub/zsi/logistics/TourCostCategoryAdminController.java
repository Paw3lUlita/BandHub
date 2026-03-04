package com.bandhub.zsi.logistics;

import com.bandhub.zsi.logistics.dto.CreateTourCategoryRequest;
import com.bandhub.zsi.logistics.dto.TourCategoryResponse;
import com.bandhub.zsi.logistics.dto.UpdateTourCategoryRequest;
import com.bandhub.zsi.shared.api.PageResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/tour-cost-categories")
@PreAuthorize("hasRole('ADMIN')")
class TourCostCategoryAdminController {
    private final TourCostCategoryAdminService service;
    TourCostCategoryAdminController(TourCostCategoryAdminService service) { this.service = service; }
    @GetMapping ResponseEntity<List<TourCategoryResponse>> getAll() { return ResponseEntity.ok(service.getAll()); }
    @GetMapping("/page") ResponseEntity<PageResponse<TourCategoryResponse>> getPage(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "name") String sortBy, @RequestParam(defaultValue = "asc") String sortDir, @RequestParam(defaultValue = "") String q) { return ResponseEntity.ok(service.getPage(page, size, sortBy, sortDir, q)); }
    @GetMapping("/{id}") ResponseEntity<TourCategoryResponse> getOne(@PathVariable UUID id) { return ResponseEntity.ok(service.getOne(id)); }
    @PostMapping ResponseEntity<Void> create(@RequestBody @Valid CreateTourCategoryRequest request) { UUID id = service.create(request); return ResponseEntity.created(URI.create("/api/admin/tour-cost-categories/" + id)).build(); }
    @PutMapping("/{id}") ResponseEntity<Void> update(@PathVariable UUID id, @RequestBody @Valid UpdateTourCategoryRequest request) { service.update(id, request); return ResponseEntity.noContent().build(); }
    @DeleteMapping("/{id}") ResponseEntity<Void> delete(@PathVariable UUID id) { service.delete(id); return ResponseEntity.noContent().build(); }
}
