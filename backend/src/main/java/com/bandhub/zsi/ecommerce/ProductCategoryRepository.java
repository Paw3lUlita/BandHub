package com.bandhub.zsi.ecommerce;

import com.bandhub.zsi.ecommerce.domain.ProductCategory;
import java.util.Optional;
import java.util.UUID;

public interface ProductCategoryRepository {
    Optional<ProductCategory> findById(UUID id);
}
