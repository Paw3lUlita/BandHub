package com.bandhub.zsi.ticketing;

import com.bandhub.zsi.shared.api.PageResponse;
import com.bandhub.zsi.ticketing.dto.CreateTicketCodeRequest;
import com.bandhub.zsi.ticketing.dto.TicketCodeResponse;
import com.bandhub.zsi.ticketing.dto.UpdateTicketCodeRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/ticket-codes")
@PreAuthorize("hasRole('ADMIN')")
class TicketCodeAdminController {

    private final TicketCodeAdminService service;

    TicketCodeAdminController(TicketCodeAdminService service) {
        this.service = service;
    }

    @GetMapping
    ResponseEntity<List<TicketCodeResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/page")
    ResponseEntity<PageResponse<TicketCodeResponse>> getPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "generatedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(defaultValue = "") String q
    ) {
        return ResponseEntity.ok(service.getPage(page, size, sortBy, sortDir, q));
    }

    @GetMapping("/{id}")
    ResponseEntity<TicketCodeResponse> getOne(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getOne(id));
    }

    @PostMapping
    ResponseEntity<Void> create(@RequestBody @Valid CreateTicketCodeRequest request) {
        UUID id = service.create(request);
        return ResponseEntity.created(URI.create("/api/admin/ticket-codes/" + id)).build();
    }

    @PutMapping("/{id}")
    ResponseEntity<Void> update(@PathVariable UUID id, @RequestBody @Valid UpdateTicketCodeRequest request) {
        service.update(id, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
