package com.bandhub.zsi.fan;

import com.bandhub.zsi.fan.dto.CreateSetlistItemRequest;
import com.bandhub.zsi.fan.dto.SetlistItemResponse;
import com.bandhub.zsi.fan.dto.UpdateSetlistItemRequest;
import com.bandhub.zsi.shared.api.PageResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/setlist-items")
@PreAuthorize("hasRole('ADMIN')")
class SetlistItemAdminController {

    private final SetlistItemAdminService service;

    SetlistItemAdminController(SetlistItemAdminService service) {
        this.service = service;
    }

    @GetMapping
    ResponseEntity<List<SetlistItemResponse>> getAll() { return ResponseEntity.ok(service.getAll()); }

    @GetMapping("/page")
    ResponseEntity<PageResponse<SetlistItemResponse>> getPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "songOrder") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(defaultValue = "") String q
    ) { return ResponseEntity.ok(service.getPage(page, size, sortBy, sortDir, q)); }

    @GetMapping("/{id}")
    ResponseEntity<SetlistItemResponse> getOne(@PathVariable UUID id) { return ResponseEntity.ok(service.getOne(id)); }

    @PostMapping
    ResponseEntity<Void> create(@RequestBody @Valid CreateSetlistItemRequest request) {
        UUID id = service.create(request);
        return ResponseEntity.created(URI.create("/api/admin/setlist-items/" + id)).build();
    }

    @PutMapping("/{id}")
    ResponseEntity<Void> update(@PathVariable UUID id, @RequestBody @Valid UpdateSetlistItemRequest request) {
        service.update(id, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
