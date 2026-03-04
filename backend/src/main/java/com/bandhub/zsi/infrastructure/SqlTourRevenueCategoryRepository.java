package com.bandhub.zsi.infrastructure;

import com.bandhub.zsi.logistics.TourRevenueCategoryRepository;
import com.bandhub.zsi.logistics.domain.TourRevenueCategory;
import org.springframework.data.jpa.repository.JpaRepository;
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
    @Override public void deleteById(UUID id) { jpaRepository.deleteById(id); }
}

interface JpaTourRevenueCategoryRepository extends JpaRepository<TourRevenueCategory, UUID> {}
