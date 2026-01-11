package com.bandhub.zsi.ticketing;

import com.bandhub.zsi.ticketing.domain.Concert;
import com.bandhub.zsi.ticketing.domain.Venue;
import com.bandhub.zsi.ticketing.dto.ConcertResponse;
import com.bandhub.zsi.ticketing.dto.CreateConcertRequest;
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
