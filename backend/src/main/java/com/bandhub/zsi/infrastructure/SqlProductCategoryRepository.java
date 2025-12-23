package com.bandhub.zsi.infrastructure;

import com.bandhub.zsi.ecommerce.ProductCategoryRepository;
import com.bandhub.zsi.ecommerce.domain.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
}

interface JpaProductCategoryRepository extends JpaRepository<ProductCategory, UUID> {}
