package com.bandhub.zsi.ecommerce;

import com.bandhub.zsi.ecommerce.domain.ProductCategory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductCategoryRepository {
    Optional<ProductCategory> findById(UUID id);

    boolean existsById(UUID id);

    void deleteById(UUID id);
    List<ProductCategory> findAll();

    ProductCategory save(ProductCategory category);
}
