package com.bandhub.zsi.ticketing;

import com.bandhub.zsi.ticketing.domain.TicketOrderItem;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TicketOrderItemRepository {
    TicketOrderItem save(TicketOrderItem ticketOrderItem);
    Optional<TicketOrderItem> findById(UUID id);
    List<TicketOrderItem> findAll();
    void deleteById(UUID id);
}
