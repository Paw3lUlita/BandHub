package com.bandhub.zsi.ticketing;

import com.bandhub.zsi.ticketing.domain.TicketPool;

import java.util.Optional;
import java.util.UUID;

public interface TicketPoolLookupRepository {
    Optional<TicketPool> findById(UUID id);
}
