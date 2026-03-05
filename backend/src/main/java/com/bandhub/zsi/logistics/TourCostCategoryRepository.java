package com.bandhub.zsi.logistics;

import com.bandhub.zsi.logistics.domain.TourCostCategory;
import com.bandhub.zsi.shared.api.PagedResult;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TourCostCategoryRepository {
    TourCostCategory save(TourCostCategory category);
    Optional<TourCostCategory> findById(UUID id);
    List<TourCostCategory> findAll();
    PagedResult<TourCostCategory> findPage(int page, int size, String sortBy, String sortDir, String q);
    void deleteById(UUID id);
}
