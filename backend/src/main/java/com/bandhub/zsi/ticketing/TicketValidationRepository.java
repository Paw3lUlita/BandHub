package com.bandhub.zsi.ticketing;

import com.bandhub.zsi.shared.api.PagedResult;
import com.bandhub.zsi.ticketing.domain.TicketValidation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TicketValidationRepository {
    TicketValidation save(TicketValidation ticketValidation);
    Optional<TicketValidation> findById(UUID id);
    List<TicketValidation> findAll();
    PagedResult<TicketValidation> findPage(int page, int size, String sortBy, String sortDir, String q);
    void deleteById(UUID id);
}
