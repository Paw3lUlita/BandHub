package com.bandhub.zsi.ticketing;

import com.bandhub.zsi.ticketing.domain.Concert;
import com.bandhub.zsi.ticketing.domain.Venue;
import com.bandhub.zsi.ticketing.dto.ConcertDetailResponse;
import com.bandhub.zsi.ticketing.dto.ConcertResponse;
import com.bandhub.zsi.ticketing.dto.CreateConcertRequest;
import com.bandhub.zsi.ticketing.dto.TicketPoolResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
