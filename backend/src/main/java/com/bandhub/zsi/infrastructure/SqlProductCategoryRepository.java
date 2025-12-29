package com.bandhub.zsi.infrastructure;

import com.bandhub.zsi.ecommerce.ProductCategoryRepository;
import com.bandhub.zsi.ecommerce.domain.Product;
import com.bandhub.zsi.ecommerce.domain.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
class SqlProductCategoryRepository implements ProductCategoryRepository {

    private final JpaProductCategoryRepository jpaRepository;

    public SqlProductCategoryRepository(JpaProductCategoryRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<ProductCategory> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public List<ProductCategory> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public ProductCategory save(ProductCategory category) {
        return jpaRepository.save(category);
    }
}

interface JpaProductCategoryRepository extends JpaRepository<ProductCategory, UUID> {}
