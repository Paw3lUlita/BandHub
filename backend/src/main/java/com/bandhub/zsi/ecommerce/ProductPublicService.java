package com.bandhub.zsi.ecommerce;

import com.bandhub.zsi.ecommerce.domain.Product;
import com.bandhub.zsi.ecommerce.dto.ProductResponse;
import com.bandhub.zsi.shared.api.PageResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Read-only service for public product catalog (fan/mobile API).
 * No admin operations - products are managed via ProductAdminService.
 */
@Service
public class ProductPublicService {

    private final ProductRepository productRepository;

    public ProductPublicService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse getProduct(UUID id) {
        return productRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + id));
    }

    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> getProductsPage(int page, int size, String sortBy, String sortDir, String query) {
        var result = productRepository.findPage(page, size, sortBy, sortDir, query);
        var content = result.content().stream().map(this::toResponse).toList();
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);
        return PageResponse.of(content, safePage, safeSize, result.totalElements(), sortBy, sortDir, query);
    }

    private ProductResponse toResponse(Product product) {
        var category = product.getCategory();
        String categoryName = category != null ? category.getName() : "Brak kategorii";
        UUID categoryId = category != null ? category.getId() : null;

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice().amount(),
                product.getPrice().currency(),
                product.getStockQuantity(),
                categoryName,
                categoryId
        );
    }
}
