package com.bandhub.zsi.ticketing;

import com.bandhub.zsi.ticketing.domain.TicketRefund;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TicketRefundRepository {
    TicketRefund save(TicketRefund ticketRefund);
    Optional<TicketRefund> findById(UUID id);
    List<TicketRefund> findAll();
    void deleteById(UUID id);
}
