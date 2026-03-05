package com.bandhub.zsi.infrastructure;

import com.bandhub.zsi.logistics.TourRevenueCategoryRepository;
import com.bandhub.zsi.logistics.domain.TourRevenueCategory;
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
class SqlTourRevenueCategoryRepository implements TourRevenueCategoryRepository {

    private final JpaTourRevenueCategoryRepository jpaRepository;

    SqlTourRevenueCategoryRepository(JpaTourRevenueCategoryRepository jpaRepository) { this.jpaRepository = jpaRepository; }
    @Override public TourRevenueCategory save(TourRevenueCategory category) { return jpaRepository.save(category); }
    @Override public Optional<TourRevenueCategory> findById(UUID id) { return jpaRepository.findById(id); }
    @Override public List<TourRevenueCategory> findAll() { return jpaRepository.findAll(); }
    @Override
    public PagedResult<TourRevenueCategory> findPage(int page, int size, String sortBy, String sortDir, String q) {
        String pattern = (q == null || q.isBlank()) ? "%" : "%" + q.trim().toLowerCase() + "%";
        Sort.Direction dir = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        String prop = switch (sortBy) {
            case "name" -> "name";
            default -> "code";
        };
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), Sort.by(dir, prop));
        var result = jpaRepository.findAllFiltered(pattern, pageable);
        return new PagedResult<>(result.getContent(), result.getTotalElements());
    }
    @Override public void deleteById(UUID id) { jpaRepository.deleteById(id); }
}

interface JpaTourRevenueCategoryRepository extends JpaRepository<TourRevenueCategory, UUID> {

    @Query("SELECT t FROM TourRevenueCategory t WHERE LOWER(COALESCE(t.code, '')) LIKE LOWER(:pattern) OR LOWER(COALESCE(t.name, '')) LIKE LOWER(:pattern)")
    org.springframework.data.domain.Page<TourRevenueCategory> findAllFiltered(@Param("pattern") String pattern, Pageable pageable);
}
