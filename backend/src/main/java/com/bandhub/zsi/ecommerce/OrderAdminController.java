package com.bandhub.zsi.ecommerce;

import com.bandhub.zsi.ecommerce.dto.OrderDetailsResponse;
import com.bandhub.zsi.ecommerce.dto.OrderSummaryResponse;
import com.bandhub.zsi.ecommerce.dto.UpdateStatusCommand;
import com.bandhub.zsi.shared.api.PageResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/orders")
@PreAuthorize("hasRole('ADMIN')")
class OrderAdminController {

    private final OrderAdminService service;

    OrderAdminController(OrderAdminService service) {
        this.service = service;
    }

    @GetMapping
    ResponseEntity<List<OrderSummaryResponse>> getAll() {
        return ResponseEntity.ok(service.getAllOrders());
    }

    @GetMapping("/page")
    ResponseEntity<PageResponse<OrderSummaryResponse>> getPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(defaultValue = "") String q,
            @RequestParam(required = false) com.bandhub.zsi.ecommerce.domain.OrderStatus status
    ) {
        return ResponseEntity.ok(service.getOrdersPage(page, size, sortBy, sortDir, q, status));
    }

    @PatchMapping("/{id}/status")
    ResponseEntity<Void> updateStatus(@PathVariable UUID id, @RequestBody @Valid UpdateStatusCommand command) {
        service.updateOrderStatus(id, command.newStatus());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    ResponseEntity<OrderDetailsResponse> getOne(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getOrderDetails(id));
    }
}