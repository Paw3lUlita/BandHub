package com.bandhub.zsi.ecommerce;

import com.bandhub.zsi.shared.Money;
import com.bandhub.zsi.ecommerce.domain.Product;
import com.bandhub.zsi.ecommerce.domain.ProductCategory;
import com.bandhub.zsi.ecommerce.dto.CreateProductRequest;
import com.bandhub.zsi.ecommerce.dto.ProductResponse;
import com.bandhub.zsi.ecommerce.dto.UpdateProductCommand;
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

    // --- ISTNIEJĄCE METODY ---

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

    public void updateProduct(UUID id, UpdateProductCommand command) {
        // 1. Pobierz agregat (Product)
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + id));

        // 2. Pobierz potrzebne zależności (Category)
        ProductCategory category = categoryRepository.findById(command.categoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found: " + command.categoryId()));

        // 3. Przygotuj Value Objects
        Money price = new Money(command.price(), command.currency());

        // 4. Wykonaj akcję domenową
        product.changeDetails(
                command.name(),
                command.description(),
                price,
                command.stockQuantity(),
                category
        );

        // 5. Koniec. Hibernate sam wyśle UPDATE dzięki @Transactional (Dirty Checking).
        // Nie trzeba wołać productRepository.save(product)!
    }

    @Transactional(readOnly = true)
    public ProductResponse getProduct(UUID id) {
        return productRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + id));
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
                categoryName,
                product.getCategory().getId()
        );
    }
}