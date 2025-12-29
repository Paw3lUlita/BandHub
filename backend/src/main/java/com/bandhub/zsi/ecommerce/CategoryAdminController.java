package com.bandhub.zsi.ecommerce;

import com.bandhub.zsi.ecommerce.dto.CreateCategoryRequest;
import com.bandhub.zsi.ecommerce.dto.ProductCategoryResponse;
import com.bandhub.zsi.ecommerce.dto.UpdateCategoryCommand;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/categories")
@PreAuthorize("hasRole('ADMIN')")
class CategoryAdminController {

    private final CategoryAdminService service;

    CategoryAdminController(CategoryAdminService service) {
        this.service = service;
    }

    @GetMapping
    ResponseEntity<List<ProductCategoryResponse>> getAll() {
        return ResponseEntity.ok(service.getAllCategories());
    }

    @GetMapping("/{id}")
    ResponseEntity<ProductCategoryResponse> getOne(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getCategory(id));
    }

    @PostMapping
    ResponseEntity<Void> create(@RequestBody CreateCategoryRequest request) {
        UUID id = service.createCategory(request);
        return ResponseEntity.created(URI.create("/api/admin/categories/" + id)).build();
    }

    @PutMapping("/{id}")
    ResponseEntity<Void> update(@PathVariable UUID id, @RequestBody CreateCategoryRequest request) {
        UpdateCategoryCommand command = new UpdateCategoryCommand(id, request.name());
        service.updateCategory(command);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}