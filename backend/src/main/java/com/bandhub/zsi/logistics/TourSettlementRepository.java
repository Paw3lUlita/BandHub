package com.bandhub.zsi.logistics;

import com.bandhub.zsi.logistics.domain.TourSettlement;
import com.bandhub.zsi.shared.api.PagedResult;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TourSettlementRepository {
    TourSettlement save(TourSettlement settlement);
    Optional<TourSettlement> findById(UUID id);
    List<TourSettlement> findAll();
    PagedResult<TourSettlement> findPage(int page, int size, String sortBy, String sortDir, String q);
    void deleteById(UUID id);
}
