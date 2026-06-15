package com.bandhub.zsi.logistics;

import com.bandhub.zsi.logistics.domain.Tour;
import com.bandhub.zsi.logistics.domain.TourLeg;
import com.bandhub.zsi.logistics.dto.CreateTourLegRequest;
import com.bandhub.zsi.logistics.dto.TourLegResponse;
import com.bandhub.zsi.logistics.dto.UpdateTourLegRequest;
import com.bandhub.zsi.shared.api.PageResponse;
import com.bandhub.zsi.ticketing.ConcertAdminService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional
public class TourLegAdminService {

    private final TourLegRepository tourLegRepository;
    private final TourRepository tourRepository;
    private final ConcertAdminService concertAdminService;

    public TourLegAdminService(
            TourLegRepository tourLegRepository,
            TourRepository tourRepository,
            ConcertAdminService concertAdminService
    ) {
        this.tourLegRepository = tourLegRepository;
        this.tourRepository = tourRepository;
        this.concertAdminService = concertAdminService;
    }

    public UUID create(CreateTourLegRequest request) {
        Tour tour = tourRepository.findById(request.tourId())
                .orElseThrow(() -> new EntityNotFoundException("Tour not found: " + request.tourId()));
        validateLegAgainstTour(tour, request.legDate(), request.plannedBudget());
        TourLeg leg = TourLeg.create(tour, request.concertId(), request.legOrder(), request.city(), request.venueName(), request.legDate(), request.plannedBudget(), request.currency());
        UUID legId = tourLegRepository.save(leg).getId();
        linkConcertToTour(request.tourId(), request.concertId());
        return legId;
    }

    public void update(UUID id, UpdateTourLegRequest request) {
        TourLeg leg = tourLegRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tour leg not found: " + id));
        UUID previousTourId = leg.getTour().getId();
        UUID previousConcertId = leg.getConcertId();

        Tour tour = tourRepository.findById(request.tourId())
                .orElseThrow(() -> new EntityNotFoundException("Tour not found: " + request.tourId()));
        validateLegAgainstTour(tour, request.legDate(), request.plannedBudget());
        leg.update(tour, request.concertId(), request.legOrder(), request.city(), request.venueName(), request.legDate(), request.plannedBudget(), request.currency());

        if (!Objects.equals(previousTourId, request.tourId()) || !Objects.equals(previousConcertId, request.concertId())) {
            unlinkConcertFromTourIfOrphaned(previousTourId, previousConcertId, id);
            linkConcertToTour(request.tourId(), request.concertId());
        }
    }

    public void delete(UUID id) {
        TourLeg leg = tourLegRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tour leg not found: " + id));
        UUID tourId = leg.getTour().getId();
        UUID concertId = leg.getConcertId();
        tourLegRepository.deleteById(id);
        unlinkConcertFromTourIfOrphaned(tourId, concertId, id);
    }

    private void linkConcertToTour(UUID tourId, UUID concertId) {
        if (concertId == null) {
            return;
        }
        if (tourLegRepository.existsByConcertIdAndTour_IdNot(concertId, tourId)) {
            throw new IllegalStateException("Koncert jest juz przypisany do innej trasy. Najpierw odepnij go od poprzedniego odcinka.");
        }
        concertAdminService.assignConcertToTour(concertId, tourId);
    }

    private void unlinkConcertFromTourIfOrphaned(UUID tourId, UUID concertId, UUID excludeLegId) {
        if (concertId == null) {
            return;
        }
        boolean linkedByAnotherLeg = tourLegRepository.existsByTour_IdAndConcertIdAndIdNot(tourId, concertId, excludeLegId);
        if (!linkedByAnotherLeg) {
            concertAdminService.unassignConcertFromTour(concertId, tourId);
        }
    }

    @Transactional(readOnly = true)
    public TourLegResponse getOne(UUID id) {
        return tourLegRepository.findById(id).map(this::toResponse).orElseThrow(() -> new EntityNotFoundException("Tour leg not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<TourLegResponse> getAll() {
        return tourLegRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<TourLegResponse> getPage(int page, int size, String sortBy, String sortDir, String query) {
        var result = tourLegRepository.findPage(page, size, sortBy, sortDir, query);
        List<TourLegResponse> content = result.content().stream().map(this::toResponse).toList();
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);
        return PageResponse.of(content, safePage, safeSize, result.totalElements(), sortBy, sortDir, query);
    }

    private TourLegResponse toResponse(TourLeg leg) {
        return new TourLegResponse(leg.getId(), leg.getTour().getId(), leg.getConcertId(), leg.getLegOrder(), leg.getCity(), leg.getVenueName(), leg.getLegDate(), leg.getPlannedBudget(), leg.getCurrency());
    }

    private static void validateLegAgainstTour(Tour tour, LocalDateTime legDate, BigDecimal plannedBudget) {
        if (plannedBudget != null && plannedBudget.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Planned budget cannot be negative");
        }
        if (legDate == null) {
            return;
        }
        LocalDateTime start = tour.getStartDate();
        LocalDateTime end = tour.getEndDate();
        if (start != null && legDate.isBefore(start)) {
            throw new IllegalArgumentException("Leg date is before tour start");
        }
        if (end != null && legDate.isAfter(end)) {
            throw new IllegalArgumentException("Leg date is after tour end");
        }
    }
}
