package com.bandhub.zsi.ticketing;

import com.bandhub.zsi.ticketing.domain.TicketOrder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TicketOrderRepository {
    TicketOrder save(TicketOrder ticketOrder);
    Optional<TicketOrder> findById(UUID id);
    List<TicketOrder> findAll();
    void deleteById(UUID id);
}
