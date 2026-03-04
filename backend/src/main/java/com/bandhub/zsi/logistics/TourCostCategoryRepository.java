package com.bandhub.zsi.logistics;

import com.bandhub.zsi.logistics.domain.TourCostCategory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TourCostCategoryRepository {
    TourCostCategory save(TourCostCategory category);
    Optional<TourCostCategory> findById(UUID id);
    List<TourCostCategory> findAll();
    void deleteById(UUID id);
}
