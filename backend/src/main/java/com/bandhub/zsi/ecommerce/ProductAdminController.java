package com.bandhub.zsi.ecommerce;

import com.bandhub.zsi.ecommerce.dto.CreateProductRequest;
import com.bandhub.zsi.ecommerce.dto.ProductResponse;
import com.bandhub.zsi.ecommerce.dto.UpdateProductCommand;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/products")
@PreAuthorize("hasRole('ADMIN')")
class ProductAdminController {

    private final ProductAdminService service;

    ProductAdminController(ProductAdminService service) {
        this.service = service;
    }

    @GetMapping
    ResponseEntity<List<ProductResponse>> getAll() {
        return ResponseEntity.ok(service.getAllProducts());
    }

    @PostMapping
    ResponseEntity<Void> create(@RequestBody CreateProductRequest request) {
        UUID id = service.createProduct(request);
        return ResponseEntity.created(URI.create("/api/admin/products/" + id)).build();
    }

    @GetMapping("/{id}")
    ResponseEntity<ProductResponse> getOne(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getProduct(id));
    }

    @PutMapping("/{id}")
    ResponseEntity<Void> update(@PathVariable UUID id, @RequestBody CreateProductRequest request) {
        UpdateProductCommand command = new UpdateProductCommand(
                request.name(),
                request.description(),
                request.price(),
                request.currency(),
                request.stockQuantity(),
                request.categoryId()
        );

        service.updateProduct(id, command);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}