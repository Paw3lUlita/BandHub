package com.bandhub.zsi.infrastructure;

import com.bandhub.zsi.logistics.TourCostCategoryRepository;
import com.bandhub.zsi.logistics.domain.TourCostCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
class SqlTourCostCategoryRepository implements TourCostCategoryRepository {

    private final JpaTourCostCategoryRepository jpaRepository;

    SqlTourCostCategoryRepository(JpaTourCostCategoryRepository jpaRepository) { this.jpaRepository = jpaRepository; }
    @Override public TourCostCategory save(TourCostCategory category) { return jpaRepository.save(category); }
    @Override public Optional<TourCostCategory> findById(UUID id) { return jpaRepository.findById(id); }
    @Override public List<TourCostCategory> findAll() { return jpaRepository.findAll(); }
    @Override public void deleteById(UUID id) { jpaRepository.deleteById(id); }
}

interface JpaTourCostCategoryRepository extends JpaRepository<TourCostCategory, UUID> {}
