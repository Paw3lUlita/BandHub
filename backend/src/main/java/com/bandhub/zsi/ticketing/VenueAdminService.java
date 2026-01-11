package com.bandhub.zsi.ticketing;

import com.bandhub.zsi.ticketing.domain.Venue;
import com.bandhub.zsi.ticketing.dto.CreateVenueRequest;
import com.bandhub.zsi.ticketing.dto.VenueResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class VenueAdminService {

    private final VenueRepository repository;

    public VenueAdminService(VenueRepository repository) {
        this.repository = repository;
    }

    public UUID createVenue(CreateVenueRequest request) {
        // Używamy Factory Method z domeny
        Venue venue = Venue.create(
                request.name(),
                request.city(),
                request.street(),
                request.capacity(),
                request.contactEmail()
        );
        return repository.save(venue).getId();
    }

    @Transactional(readOnly = true)
    public List<VenueResponse> getAllVenues() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public void deleteVenue(UUID id) {
        repository.deleteById(id);
    }

    private VenueResponse toResponse(Venue venue) {
        return new VenueResponse(
                venue.getId(),
                venue.getName(),
                venue.getCity(),
                venue.getStreet(),
                venue.getCapacity(),
                venue.getContactEmail()
        );
    }
}
