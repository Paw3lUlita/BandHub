package com.bandhub.zsi.reporting;

import com.bandhub.zsi.reporting.dto.CreateReportRunRequest;
import com.bandhub.zsi.reporting.dto.ReportRunResponse;
import com.bandhub.zsi.reporting.dto.UpdateReportRunRequest;
import com.bandhub.zsi.shared.api.PageResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/report-runs")
@PreAuthorize("hasRole('ADMIN')")
class ReportRunAdminController {

    private final ReportRunAdminService service;

    ReportRunAdminController(ReportRunAdminService service) {
        this.service = service;
    }

    @GetMapping
    ResponseEntity<List<ReportRunResponse>> getAll() { return ResponseEntity.ok(service.getAll()); }

    @GetMapping("/page")
    ResponseEntity<PageResponse<ReportRunResponse>> getPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(defaultValue = "") String q
    ) { return ResponseEntity.ok(service.getPage(page, size, sortBy, sortDir, q)); }

    @GetMapping("/{id}")
    ResponseEntity<ReportRunResponse> getOne(@PathVariable UUID id) { return ResponseEntity.ok(service.getOne(id)); }

    @PostMapping
    ResponseEntity<Void> create(@RequestBody @Valid CreateReportRunRequest request) {
        UUID id = service.create(request);
        return ResponseEntity.created(URI.create("/api/admin/report-runs/" + id)).build();
    }

    @PutMapping("/{id}")
    ResponseEntity<Void> update(@PathVariable UUID id, @RequestBody @Valid UpdateReportRunRequest request) {
        service.update(id, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
