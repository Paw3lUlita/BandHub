package com.bandhub.zsi.ticketing;

import com.bandhub.zsi.shared.api.PagedResult;
import com.bandhub.zsi.ticketing.domain.TicketOrderItem;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TicketOrderItemRepository {
    TicketOrderItem save(TicketOrderItem ticketOrderItem);
    Optional<TicketOrderItem> findById(UUID id);
    List<TicketOrderItem> findAll();
    PagedResult<TicketOrderItem> findPage(int page, int size, String sortBy, String sortDir, String q);
    void deleteById(UUID id);
}
