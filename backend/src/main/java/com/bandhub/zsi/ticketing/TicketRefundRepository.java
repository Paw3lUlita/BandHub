package com.bandhub.zsi.ticketing;

import com.bandhub.zsi.shared.api.PagedResult;
import com.bandhub.zsi.ticketing.domain.TicketRefund;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TicketRefundRepository {
    TicketRefund save(TicketRefund ticketRefund);
    Optional<TicketRefund> findById(UUID id);
    List<TicketRefund> findAll();
    PagedResult<TicketRefund> findPage(int page, int size, String sortBy, String sortDir, String q);
    void deleteById(UUID id);
}
