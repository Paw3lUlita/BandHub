package com.bandhub.zsi.ticketing;

import com.bandhub.zsi.ticketing.domain.TicketValidation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TicketValidationRepository {
    TicketValidation save(TicketValidation ticketValidation);
    Optional<TicketValidation> findById(UUID id);
    List<TicketValidation> findAll();
    void deleteById(UUID id);
}
