package com.bandhub.zsi.logistics;

import com.bandhub.zsi.logistics.domain.Tour;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TourRepository {
    Tour save(Tour tour);
    Optional<Tour> findById(UUID id);
    List<Tour> findAll();
    void deleteById(UUID id);
}
