package com.bandhub.zsi.ecommerce;

import com.bandhub.zsi.ecommerce.dto.ProductResponse;
import com.bandhub.zsi.shared.api.PageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Public REST API for product catalog (fan/mobile).
 * No authentication required - permitAll in SecurityConfig.
 */
@RestController
@RequestMapping("/api/public/products")
class ProductPublicController {

    private final ProductPublicService service;

    ProductPublicController(ProductPublicService service) {
        this.service = service;
    }

    @GetMapping
    ResponseEntity<List<ProductResponse>> getAll() {
        return ResponseEntity.ok(service.getAllProducts());
    }

    @GetMapping("/page")
    ResponseEntity<PageResponse<ProductResponse>> getPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(defaultValue = "") String q
    ) {
        return ResponseEntity.ok(service.getProductsPage(page, size, sortBy, sortDir, q));
    }

    @GetMapping("/{id}")
    ResponseEntity<ProductResponse> getOne(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getProduct(id));
    }
}
