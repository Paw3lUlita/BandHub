package com.bandhub.zsi.ticketing;

import com.bandhub.zsi.shared.api.PagedResult;
import com.bandhub.zsi.ticketing.domain.TicketOrder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TicketOrderRepository {
    TicketOrder save(TicketOrder ticketOrder);
    Optional<TicketOrder> findById(UUID id);
    List<TicketOrder> findAll();
    PagedResult<TicketOrder> findPage(int page, int size, String sortBy, String sortDir, String q);
    void deleteById(UUID id);
}
