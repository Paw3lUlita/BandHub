package com.bandhub.zsi.ecommerce;

import com.bandhub.zsi.ecommerce.domain.ProductCategory;
import com.bandhub.zsi.ecommerce.dto.CreateCategoryRequest;
import com.bandhub.zsi.ecommerce.dto.ProductCategoryResponse;
import com.bandhub.zsi.ecommerce.dto.UpdateCategoryCommand;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CategoryAdminService {

    private final ProductCategoryRepository repository;

    public CategoryAdminService(ProductCategoryRepository repository) {
        this.repository = repository;
    }

    public UUID createCategory(CreateCategoryRequest request) {
        ProductCategory category = ProductCategory.create(request.name());
        return repository.save(category).getId();
    }

    public void updateCategory(UpdateCategoryCommand command) {
        ProductCategory category = repository.findById(command.id())
                .orElseThrow(() -> new EntityNotFoundException("Category not found: " + command.id()));

        category.changeName(command.name());
    }

    public void deleteCategory(UUID id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Category not found: " + id);
        }
        repository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public ProductCategoryResponse getCategory(UUID id) {
        return repository.findById(id)
                .map(c -> new ProductCategoryResponse(c.getId(), c.getName()))
                .orElseThrow(() -> new EntityNotFoundException("Category not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<ProductCategoryResponse> getAllCategories() {
        return repository.findAll().stream()
                .map(c -> new ProductCategoryResponse(c.getId(), c.getName()))
                .toList();
    }
}