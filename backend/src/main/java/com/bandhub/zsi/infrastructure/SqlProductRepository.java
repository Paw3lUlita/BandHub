package com.bandhub.zsi.infrastructure;

import com.bandhub.zsi.ecommerce.ProductRepository;
import com.bandhub.zsi.ecommerce.domain.Product;
import com.bandhub.zsi.shared.api.PagedResult;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    public PagedResult<Product> findPage(int page, int size, String sortBy, String sortDir, String q) {
        String pattern = (q == null || q.isBlank()) ? "%" : "%" + q.trim().toLowerCase() + "%";
        Sort.Direction dir = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        String prop = switch (sortBy) {
            case "price" -> "price.amount";
            case "stockQuantity" -> "stockQuantity";
            case "categoryName" -> "category.name";
            default -> "name";
        };
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), Sort.by(dir, prop));
        var result = jpaRepository.findAllFiltered(pattern, pageable);
        return new PagedResult<>(result.getContent(), result.getTotalElements());
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}

interface JpaProductRepository extends JpaRepository<Product, UUID> {

    @Query("SELECT p FROM Product p LEFT JOIN p.category c WHERE " +
            "LOWER(p.name) LIKE LOWER(:pattern) OR " +
            "LOWER(COALESCE(p.description, '')) LIKE LOWER(:pattern) OR " +
            "LOWER(COALESCE(c.name, '')) LIKE LOWER(:pattern)")
    org.springframework.data.domain.Page<Product> findAllFiltered(@Param("pattern") String pattern, Pageable pageable);
}