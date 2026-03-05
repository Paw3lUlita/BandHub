package com.bandhub.zsi.logistics;

import com.bandhub.zsi.logistics.domain.TourRevenueCategory;
import com.bandhub.zsi.shared.api.PagedResult;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TourRevenueCategoryRepository {
    TourRevenueCategory save(TourRevenueCategory category);
    Optional<TourRevenueCategory> findById(UUID id);
    List<TourRevenueCategory> findAll();
    PagedResult<TourRevenueCategory> findPage(int page, int size, String sortBy, String sortDir, String q);
    void deleteById(UUID id);
}
