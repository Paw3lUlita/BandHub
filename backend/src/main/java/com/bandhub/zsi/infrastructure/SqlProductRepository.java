package com.bandhub.zsi.infrastructure;

import com.bandhub.zsi.ecommerce.ProductRepository;
import com.bandhub.zsi.ecommerce.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
class SqlProductRepository implements ProductRepository {

    private final JpaProductRepository jpaRepository;

    public SqlProductRepository(JpaProductRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Product save(Product product) {
        return jpaRepository.save(product);
    }

    @Override
    public Optional<Product> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Product> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}

interface JpaProductRepository extends JpaRepository<Product, UUID> {}