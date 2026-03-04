package com.bandhub.zsi.logistics;

import com.bandhub.zsi.logistics.domain.TourRevenueCategory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TourRevenueCategoryRepository {
    TourRevenueCategory save(TourRevenueCategory category);
    Optional<TourRevenueCategory> findById(UUID id);
    List<TourRevenueCategory> findAll();
    void deleteById(UUID id);
}
