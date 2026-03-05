package com.bandhub.zsi.ecommerce;

import com.bandhub.zsi.ecommerce.domain.Product;
import com.bandhub.zsi.shared.api.PagedResult;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {
    Product save(Product product);
    Optional<Product> findById(UUID id);
    List<Product> findAll();
    PagedResult<Product> findPage(int page, int size, String sortBy, String sortDir, String q);
    void deleteById(UUID id);
}