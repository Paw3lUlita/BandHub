package com.bandhub.zsi.ticketing;

import com.bandhub.zsi.shared.api.PageResponse;
import com.bandhub.zsi.ticketing.dto.CreateTicketOrderItemRequest;
import com.bandhub.zsi.ticketing.dto.TicketOrderItemResponse;
import com.bandhub.zsi.ticketing.dto.UpdateTicketOrderItemRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/ticket-order-items")
@PreAuthorize("hasRole('ADMIN')")
class TicketOrderItemAdminController {

    private final TicketOrderItemAdminService service;

    TicketOrderItemAdminController(TicketOrderItemAdminService service) {
        this.service = service;
    }

    @GetMapping
    ResponseEntity<List<TicketOrderItemResponse>> getAll() { return ResponseEntity.ok(service.getAll()); }

    @GetMapping("/page")
    ResponseEntity<PageResponse<TicketOrderItemResponse>> getPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(defaultValue = "") String q
    ) { return ResponseEntity.ok(service.getPage(page, size, sortBy, sortDir, q)); }

    @GetMapping("/{id}")
    ResponseEntity<TicketOrderItemResponse> getOne(@PathVariable UUID id) { return ResponseEntity.ok(service.getOne(id)); }

    @PostMapping
    ResponseEntity<Void> create(@RequestBody @Valid CreateTicketOrderItemRequest request) {
        UUID id = service.create(request);
        return ResponseEntity.created(URI.create("/api/admin/ticket-order-items/" + id)).build();
    }

    @PutMapping("/{id}")
    ResponseEntity<Void> update(@PathVariable UUID id, @RequestBody @Valid UpdateTicketOrderItemRequest request) {
        service.update(id, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
