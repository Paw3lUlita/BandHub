package com.bandhub.zsi.ecommerce;

import com.bandhub.zsi.ecommerce.domain.Money;
import com.bandhub.zsi.ecommerce.domain.Product;
import com.bandhub.zsi.ecommerce.domain.ProductCategory;
import com.bandhub.zsi.ecommerce.dto.CreateProductRequest;
import com.bandhub.zsi.ecommerce.dto.ProductResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ProductAdminService {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository categoryRepository;

    public ProductAdminService(ProductRepository productRepository, ProductCategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public UUID createProduct(CreateProductRequest request) {
        ProductCategory category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new EntityNotFoundException("Kategoria o podanym ID nie istnieje: " + request.categoryId()));

        Money price = new Money(request.price(), request.currency());

        Product product = Product.create(
                request.name(),
                request.description(),
                price,
                request.stockQuantity(),
                category
        );

        Product savedProduct = productRepository.save(product);
        return savedProduct.getId();
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public void deleteProduct(UUID id) {
        if (productRepository.findById(id).isEmpty()) {
            throw new EntityNotFoundException("Produkt o podanym ID nie istnieje: " + id);
        }
        productRepository.deleteById(id);
    }

    private ProductResponse toResponse(Product product) {
        String categoryName = (product.getCategory() != null) ? product.getCategory().getName() : "Brak kategorii";

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice().amount(),
                product.getPrice().currency(),
                product.getStockQuantity(),
                categoryName
        );
    }
}