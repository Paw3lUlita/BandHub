package com.bandhub.zsi.fan;

import com.bandhub.zsi.fan.dto.CreateFanNotificationReadRequest;
import com.bandhub.zsi.fan.dto.FanNotificationReadResponse;
import com.bandhub.zsi.fan.dto.UpdateFanNotificationReadRequest;
import com.bandhub.zsi.shared.api.PageResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/fan-notification-reads")
@PreAuthorize("hasRole('ADMIN')")
class FanNotificationReadAdminController {

    private final FanNotificationReadAdminService service;

    FanNotificationReadAdminController(FanNotificationReadAdminService service) {
        this.service = service;
    }

    @GetMapping
    ResponseEntity<List<FanNotificationReadResponse>> getAll() { return ResponseEntity.ok(service.getAll()); }

    @GetMapping("/page")
    ResponseEntity<PageResponse<FanNotificationReadResponse>> getPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "readAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(defaultValue = "") String q
    ) { return ResponseEntity.ok(service.getPage(page, size, sortBy, sortDir, q)); }

    @GetMapping("/{id}")
    ResponseEntity<FanNotificationReadResponse> getOne(@PathVariable UUID id) { return ResponseEntity.ok(service.getOne(id)); }

    @PostMapping
    ResponseEntity<Void> create(@RequestBody @Valid CreateFanNotificationReadRequest request) {
        UUID id = service.create(request);
        return ResponseEntity.created(URI.create("/api/admin/fan-notification-reads/" + id)).build();
    }

    @PutMapping("/{id}")
    ResponseEntity<Void> update(@PathVariable UUID id, @RequestBody @Valid UpdateFanNotificationReadRequest request) {
        service.update(id, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
