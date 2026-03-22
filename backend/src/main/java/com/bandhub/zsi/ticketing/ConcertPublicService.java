package com.bandhub.zsi.ticketing;

import com.bandhub.zsi.ticketing.dto.ConcertDetailResponse;
import com.bandhub.zsi.ticketing.dto.ConcertResponse;
import com.bandhub.zsi.shared.api.PageResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Read-only public API for concerts (fan / future mobile). Delegates to {@link ConcertAdminService}.
 */
@Service
public class ConcertPublicService {

    private final ConcertAdminService concertAdminService;

    public ConcertPublicService(ConcertAdminService concertAdminService) {
        this.concertAdminService = concertAdminService;
    }

    @Transactional(readOnly = true)
    public List<ConcertResponse> getAllConcerts() {
        return concertAdminService.getAllConcerts();
    }

    @Transactional(readOnly = true)
    public PageResponse<ConcertResponse> getConcertsPage(int page, int size, String sortBy, String sortDir, String q) {
        return concertAdminService.getConcertsPage(page, size, sortBy, sortDir, q);
    }

    @Transactional(readOnly = true)
    public ConcertDetailResponse getConcert(UUID id) {
        return concertAdminService.getConcert(id);
    }
}
