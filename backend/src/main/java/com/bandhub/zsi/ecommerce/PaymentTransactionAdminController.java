package com.bandhub.zsi.ecommerce;

import com.bandhub.zsi.ecommerce.dto.CreatePaymentTransactionRequest;
import com.bandhub.zsi.ecommerce.dto.PaymentTransactionResponse;
import com.bandhub.zsi.ecommerce.dto.UpdatePaymentTransactionRequest;
import com.bandhub.zsi.shared.api.PageResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/payment-transactions")
@PreAuthorize("hasRole('ADMIN')")
class PaymentTransactionAdminController {

    private final PaymentTransactionAdminService service;

    PaymentTransactionAdminController(PaymentTransactionAdminService service) {
        this.service = service;
    }

    @GetMapping
    ResponseEntity<List<PaymentTransactionResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/page")
    ResponseEntity<PageResponse<PaymentTransactionResponse>> getPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(defaultValue = "") String q
    ) {
        return ResponseEntity.ok(service.getPage(page, size, sortBy, sortDir, q));
    }

    @GetMapping("/{id}")
    ResponseEntity<PaymentTransactionResponse> getOne(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getOne(id));
    }

    @PostMapping
    ResponseEntity<Void> create(@RequestBody @Valid CreatePaymentTransactionRequest request) {
        UUID id = service.create(request);
        return ResponseEntity.created(URI.create("/api/admin/payment-transactions/" + id)).build();
    }

    @PutMapping("/{id}")
    ResponseEntity<Void> update(@PathVariable UUID id, @RequestBody @Valid UpdatePaymentTransactionRequest request) {
        service.update(id, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
