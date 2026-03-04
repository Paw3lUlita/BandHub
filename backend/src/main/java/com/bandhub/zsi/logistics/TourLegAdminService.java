package com.bandhub.zsi.logistics;

import com.bandhub.zsi.logistics.domain.Tour;
import com.bandhub.zsi.logistics.domain.TourLeg;
import com.bandhub.zsi.logistics.dto.CreateTourLegRequest;
import com.bandhub.zsi.logistics.dto.TourLegResponse;
import com.bandhub.zsi.logistics.dto.UpdateTourLegRequest;
import com.bandhub.zsi.shared.api.PageResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class TourLegAdminService {

    private final TourLegRepository tourLegRepository;
    private final TourRepository tourRepository;

    public TourLegAdminService(TourLegRepository tourLegRepository, TourRepository tourRepository) {
        this.tourLegRepository = tourLegRepository;
        this.tourRepository = tourRepository;
    }

    public UUID create(CreateTourLegRequest request) {
        Tour tour = tourRepository.findById(request.tourId())
                .orElseThrow(() -> new EntityNotFoundException("Tour not found: " + request.tourId()));
        TourLeg leg = TourLeg.create(tour, request.concertId(), request.legOrder(), request.city(), request.venueName(), request.legDate(), request.plannedBudget(), request.currency());
        return tourLegRepository.save(leg).getId();
    }

    public void update(UUID id, UpdateTourLegRequest request) {
        TourLeg leg = tourLegRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tour leg not found: " + id));
        Tour tour = tourRepository.findById(request.tourId())
                .orElseThrow(() -> new EntityNotFoundException("Tour not found: " + request.tourId()));
        leg.update(tour, request.concertId(), request.legOrder(), request.city(), request.venueName(), request.legDate(), request.plannedBudget(), request.currency());
    }

    public void delete(UUID id) {
        if (tourLegRepository.findById(id).isEmpty()) {
            throw new EntityNotFoundException("Tour leg not found: " + id);
        }
        tourLegRepository.deleteById(id);
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
        String normalizedQuery = query == null ? "" : query.trim().toLowerCase();
        boolean descending = "desc".equalsIgnoreCase(sortDir);
        List<TourLegResponse> filtered = tourLegRepository.findAll().stream()
                .map(this::toResponse)
                .filter(item -> normalizedQuery.isBlank()
                        || item.city().toLowerCase().contains(normalizedQuery)
                        || (item.venueName() != null && item.venueName().toLowerCase().contains(normalizedQuery))
                        || item.tourId().toString().toLowerCase().contains(normalizedQuery))
                .sorted(resolveComparator(sortBy, descending))
                .toList();
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);
        int fromIndex = safePage * safeSize;
        int toIndex = Math.min(fromIndex + safeSize, filtered.size());
        List<TourLegResponse> content = fromIndex >= filtered.size() ? List.of() : filtered.subList(fromIndex, toIndex);
        return PageResponse.of(content, safePage, safeSize, filtered.size(), sortBy, sortDir, query);
    }

    private Comparator<TourLegResponse> resolveComparator(String sortBy, boolean descending) {
        Comparator<TourLegResponse> comparator = switch (sortBy) {
            case "city" -> Comparator.comparing(TourLegResponse::city, String.CASE_INSENSITIVE_ORDER);
            case "legOrder" -> Comparator.comparing(TourLegResponse::legOrder);
            case "legDate" -> Comparator.comparing(TourLegResponse::legDate, Comparator.nullsLast(Comparator.naturalOrder()));
            default -> Comparator.comparing(TourLegResponse::legOrder);
        };
        return descending ? comparator.reversed() : comparator;
    }

    private TourLegResponse toResponse(TourLeg leg) {
        return new TourLegResponse(leg.getId(), leg.getTour().getId(), leg.getConcertId(), leg.getLegOrder(), leg.getCity(), leg.getVenueName(), leg.getLegDate(), leg.getPlannedBudget(), leg.getCurrency());
    }
}
