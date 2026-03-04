package com.bandhub.zsi.ticketing;

import com.bandhub.zsi.ticketing.domain.Concert;
import com.bandhub.zsi.ticketing.domain.Venue;
import com.bandhub.zsi.ticketing.dto.ConcertDetailResponse;
import com.bandhub.zsi.ticketing.dto.ConcertResponse;
import com.bandhub.zsi.ticketing.dto.CreateConcertRequest;
import com.bandhub.zsi.ticketing.dto.TicketPoolResponse;
import com.bandhub.zsi.shared.api.PageResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ConcertAdminService {

    private final ConcertRepository concertRepository;
    private final VenueRepository venueRepository;

    public ConcertAdminService(ConcertRepository concertRepository, VenueRepository venueRepository) {
        this.concertRepository = concertRepository;
        this.venueRepository = venueRepository;
    }

    public UUID createConcert(CreateConcertRequest request) {
        Venue venue = venueRepository.findById(request.venueId())
                .orElseThrow(() -> new EntityNotFoundException("Venue not found: " + request.venueId()));

        Concert concert = Concert.plan(
                request.name(),
                request.date(),
                venue
        );

        concert.updateDetails(request.name(), request.date(), request.description(), request.imageUrl(), venue);

        if (request.ticketPools() != null) {
            request.ticketPools().forEach(poolReq ->
                    concert.configureTicketPool(
                            poolReq.name(),
                            poolReq.price(),
                            poolReq.currency(),
                            poolReq.totalQuantity()
                    )
            );
        }

        return concertRepository.save(concert).getId();
    }

    @Transactional(readOnly = true)
    public List<ConcertResponse> getAllConcerts() {
        return concertRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ConcertDetailResponse getConcert(UUID id) {
        Concert concert = concertRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Concert not found: " + id));

        List<TicketPoolResponse> pools = concert.getTicketPools().stream()
                .map(pool -> new TicketPoolResponse(
                        pool.getId(),
                        pool.getName(),
                        pool.getPrice().amount(),
                        pool.getPrice().currency(),
                        pool.getTotalQuantity(),
                        pool.getRemainingQuantity()
                ))
                .toList();

        return new ConcertDetailResponse(
                concert.getId(),
                concert.getName(),
                concert.getDate(),
                concert.getDescription(),
                concert.getImageUrl(),
                concert.getVenue().getName(),
                concert.getVenue().getCity(),
                pools
        );
    }

    @Transactional(readOnly = true)
    public PageResponse<ConcertResponse> getConcertsPage(int page, int size, String sortBy, String sortDir, String query) {
        String normalizedQuery = query == null ? "" : query.trim().toLowerCase();
        boolean descending = "desc".equalsIgnoreCase(sortDir);

        List<ConcertResponse> filtered = concertRepository.findAll().stream()
                .map(this::toResponse)
                .filter(concert -> normalizedQuery.isBlank()
                        || concert.name().toLowerCase().contains(normalizedQuery)
                        || concert.venueName().toLowerCase().contains(normalizedQuery)
                        || concert.city().toLowerCase().contains(normalizedQuery))
                .sorted(resolveComparator(sortBy, descending))
                .toList();

        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);
        int fromIndex = safePage * safeSize;
        int toIndex = Math.min(fromIndex + safeSize, filtered.size());

        List<ConcertResponse> content = fromIndex >= filtered.size()
                ? List.of()
                : filtered.subList(fromIndex, toIndex);

        return PageResponse.of(content, safePage, safeSize, filtered.size(), sortBy, sortDir, query);
    }

    private Comparator<ConcertResponse> resolveComparator(String sortBy, boolean descending) {
        Comparator<ConcertResponse> comparator = switch (sortBy) {
            case "venueName" -> Comparator.comparing(ConcertResponse::venueName, String.CASE_INSENSITIVE_ORDER);
            case "city" -> Comparator.comparing(ConcertResponse::city, String.CASE_INSENSITIVE_ORDER);
            case "name" -> Comparator.comparing(ConcertResponse::name, String.CASE_INSENSITIVE_ORDER);
            case "date" -> Comparator.comparing(ConcertResponse::date);
            default -> Comparator.comparing(ConcertResponse::date);
        };

        return descending ? comparator.reversed() : comparator;
    }

    private ConcertResponse toResponse(Concert concert) {
        return new ConcertResponse(
                concert.getId(),
                concert.getName(),
                concert.getDate(),
                concert.getVenue().getName(),
                concert.getVenue().getCity()
        );
    }
}
