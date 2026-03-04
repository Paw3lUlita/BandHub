package com.bandhub.zsi.ecommerce;

import com.bandhub.zsi.ecommerce.dto.CreatePaymentRequest;
import com.bandhub.zsi.ecommerce.dto.PaymentResponse;
import com.bandhub.zsi.ecommerce.dto.UpdatePaymentRequest;
import com.bandhub.zsi.shared.api.PageResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/payments")
@PreAuthorize("hasRole('ADMIN')")
class PaymentAdminController {

    private final PaymentAdminService service;

    PaymentAdminController(PaymentAdminService service) {
        this.service = service;
    }

    @GetMapping
    ResponseEntity<List<PaymentResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/page")
    ResponseEntity<PageResponse<PaymentResponse>> getPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(defaultValue = "") String q
    ) {
        return ResponseEntity.ok(service.getPage(page, size, sortBy, sortDir, q));
    }

    @GetMapping("/{id}")
    ResponseEntity<PaymentResponse> getOne(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getOne(id));
    }

    @PostMapping
    ResponseEntity<Void> create(@RequestBody @Valid CreatePaymentRequest request) {
        UUID id = service.create(request);
        return ResponseEntity.created(URI.create("/api/admin/payments/" + id)).build();
    }

    @PutMapping("/{id}")
    ResponseEntity<Void> update(@PathVariable UUID id, @RequestBody @Valid UpdatePaymentRequest request) {
        service.update(id, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
