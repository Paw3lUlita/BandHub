package com.bandhub.zsi.ticketing;

import com.bandhub.zsi.ticketing.domain.Ticket;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TicketRepository {
    Ticket save(Ticket ticket);

    Optional<Ticket> findById(UUID id);

    List<Ticket> findByTicketOrderId(UUID ticketOrderId);
}
