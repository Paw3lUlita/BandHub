package com.bandhub.zsi.logistics;

import com.bandhub.zsi.logistics.domain.TourSettlement;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TourSettlementRepository {
    TourSettlement save(TourSettlement settlement);
    Optional<TourSettlement> findById(UUID id);
    List<TourSettlement> findAll();
    void deleteById(UUID id);
}
