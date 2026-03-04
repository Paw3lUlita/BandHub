package com.bandhub.zsi.fan;

import com.bandhub.zsi.fan.dto.CreateFanFavoriteRequest;
import com.bandhub.zsi.fan.dto.FanFavoriteResponse;
import com.bandhub.zsi.fan.dto.UpdateFanFavoriteRequest;
import com.bandhub.zsi.shared.api.PageResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/fan-favorites")
@PreAuthorize("hasRole('ADMIN')")
class FanFavoriteAdminController {

    private final FanFavoriteAdminService service;

    FanFavoriteAdminController(FanFavoriteAdminService service) {
        this.service = service;
    }

    @GetMapping
    ResponseEntity<List<FanFavoriteResponse>> getAll() { return ResponseEntity.ok(service.getAll()); }

    @GetMapping("/page")
    ResponseEntity<PageResponse<FanFavoriteResponse>> getPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(defaultValue = "") String q
    ) { return ResponseEntity.ok(service.getPage(page, size, sortBy, sortDir, q)); }

    @GetMapping("/{id}")
    ResponseEntity<FanFavoriteResponse> getOne(@PathVariable UUID id) { return ResponseEntity.ok(service.getOne(id)); }

    @PostMapping
    ResponseEntity<Void> create(@RequestBody @Valid CreateFanFavoriteRequest request) {
        UUID id = service.create(request);
        return ResponseEntity.created(URI.create("/api/admin/fan-favorites/" + id)).build();
    }

    @PutMapping("/{id}")
    ResponseEntity<Void> update(@PathVariable UUID id, @RequestBody @Valid UpdateFanFavoriteRequest request) {
        service.update(id, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
