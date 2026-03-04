package com.bandhub.zsi.ticketing;

import com.bandhub.zsi.shared.api.PageResponse;
import com.bandhub.zsi.ticketing.dto.CreateTicketOrderRequest;
import com.bandhub.zsi.ticketing.dto.TicketOrderResponse;
import com.bandhub.zsi.ticketing.dto.UpdateTicketOrderRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/ticket-orders")
@PreAuthorize("hasRole('ADMIN')")
class TicketOrderAdminController {

    private final TicketOrderAdminService service;

    TicketOrderAdminController(TicketOrderAdminService service) {
        this.service = service;
    }

    @GetMapping
    ResponseEntity<List<TicketOrderResponse>> getAll() { return ResponseEntity.ok(service.getAll()); }

    @GetMapping("/page")
    ResponseEntity<PageResponse<TicketOrderResponse>> getPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(defaultValue = "") String q
    ) { return ResponseEntity.ok(service.getPage(page, size, sortBy, sortDir, q)); }

    @GetMapping("/{id}")
    ResponseEntity<TicketOrderResponse> getOne(@PathVariable UUID id) { return ResponseEntity.ok(service.getOne(id)); }

    @PostMapping
    ResponseEntity<Void> create(@RequestBody @Valid CreateTicketOrderRequest request) {
        UUID id = service.create(request);
        return ResponseEntity.created(URI.create("/api/admin/ticket-orders/" + id)).build();
    }

    @PutMapping("/{id}")
    ResponseEntity<Void> update(@PathVariable UUID id, @RequestBody @Valid UpdateTicketOrderRequest request) {
        service.update(id, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
