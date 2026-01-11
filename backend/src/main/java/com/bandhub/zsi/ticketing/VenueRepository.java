package com.bandhub.zsi.ticketing;

import com.bandhub.zsi.ticketing.domain.Venue;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VenueRepository {
    Venue save(Venue venue);
    List<Venue> findAll();
    Optional<Venue> findById(UUID id);
    void deleteById(UUID id);
}
