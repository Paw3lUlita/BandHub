package com.bandhub.zsi.reporting;

import com.bandhub.zsi.reporting.dto.CreateExportJobRequest;
import com.bandhub.zsi.reporting.dto.ExportJobResponse;
import com.bandhub.zsi.reporting.dto.UpdateExportJobRequest;
import com.bandhub.zsi.shared.api.PageResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/export-jobs")
@PreAuthorize("hasRole('ADMIN')")
class ExportJobAdminController {

    private final ExportJobAdminService service;

    ExportJobAdminController(ExportJobAdminService service) {
        this.service = service;
    }

    @GetMapping
    ResponseEntity<List<ExportJobResponse>> getAll() { return ResponseEntity.ok(service.getAll()); }

    @GetMapping("/page")
    ResponseEntity<PageResponse<ExportJobResponse>> getPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(defaultValue = "") String q
    ) { return ResponseEntity.ok(service.getPage(page, size, sortBy, sortDir, q)); }

    @GetMapping("/{id}")
    ResponseEntity<ExportJobResponse> getOne(@PathVariable UUID id) { return ResponseEntity.ok(service.getOne(id)); }

    @PostMapping
    ResponseEntity<Void> create(@RequestBody @Valid CreateExportJobRequest request) {
        UUID id = service.create(request);
        return ResponseEntity.created(URI.create("/api/admin/export-jobs/" + id)).build();
    }

    @PutMapping("/{id}")
    ResponseEntity<Void> update(@PathVariable UUID id, @RequestBody @Valid UpdateExportJobRequest request) {
        service.update(id, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
