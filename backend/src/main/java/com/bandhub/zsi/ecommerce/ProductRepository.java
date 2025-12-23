package com.bandhub.zsi.ecommerce;

import com.bandhub.zsi.ecommerce.domain.Product;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {
    Product save(Product product);
    Optional<Product> findById(UUID id);
    List<Product> findAll();
    void deleteById(UUID id);
}