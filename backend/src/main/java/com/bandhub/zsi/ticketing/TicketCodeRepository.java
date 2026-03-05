package com.bandhub.zsi.ticketing;

import com.bandhub.zsi.shared.api.PagedResult;
import com.bandhub.zsi.ticketing.domain.TicketCode;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TicketCodeRepository {
    TicketCode save(TicketCode ticketCode);
    Optional<TicketCode> findById(UUID id);
    List<TicketCode> findAll();
    PagedResult<TicketCode> findPage(int page, int size, String sortBy, String sortDir, String q);
    void deleteById(UUID id);
}
