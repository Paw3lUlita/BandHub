package com.bandhub.zsi.ticketing;

import com.bandhub.zsi.ticketing.domain.Concert;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConcertRepository {
    Concert save(Concert concert);
    Optional<Concert> findById(UUID id);
    List<Concert> findAll();
    void deleteById(UUID id);
}
