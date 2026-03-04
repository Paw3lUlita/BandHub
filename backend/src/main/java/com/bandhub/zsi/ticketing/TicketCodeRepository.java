package com.bandhub.zsi.ticketing;

import com.bandhub.zsi.ticketing.domain.TicketCode;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TicketCodeRepository {
    TicketCode save(TicketCode ticketCode);
    Optional<TicketCode> findById(UUID id);
    List<TicketCode> findAll();
    void deleteById(UUID id);
}
