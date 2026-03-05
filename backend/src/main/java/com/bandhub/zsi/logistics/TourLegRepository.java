package com.bandhub.zsi.logistics;

import com.bandhub.zsi.logistics.domain.TourLeg;
import com.bandhub.zsi.shared.api.PagedResult;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TourLegRepository {
    TourLeg save(TourLeg tourLeg);
    Optional<TourLeg> findById(UUID id);
    List<TourLeg> findAll();
    PagedResult<TourLeg> findPage(int page, int size, String sortBy, String sortDir, String q);
    void deleteById(UUID id);
}
