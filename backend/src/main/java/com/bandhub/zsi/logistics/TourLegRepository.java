package com.bandhub.zsi.logistics;

import com.bandhub.zsi.logistics.domain.TourLeg;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TourLegRepository {
    TourLeg save(TourLeg tourLeg);
    Optional<TourLeg> findById(UUID id);
    List<TourLeg> findAll();
    void deleteById(UUID id);
}
