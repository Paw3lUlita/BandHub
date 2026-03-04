package com.bandhub.zsi.logistics;

import com.bandhub.zsi.logistics.domain.Tour;
import com.bandhub.zsi.logistics.domain.TourCost;
import com.bandhub.zsi.logistics.domain.TourRevenue;
import com.bandhub.zsi.logistics.dto.*;
import com.bandhub.zsi.shared.api.PageResponse;
import com.bandhub.zsi.shared.Money;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class LogisticsAdminService {

    private final TourRepository tourRepository;

    public LogisticsAdminService(TourRepository tourRepository) {
        this.tourRepository = tourRepository;
    }

    public UUID createTour(CreateTourRequest request) {
        Tour tour = Tour.plan(
                request.name(),
                request.startDate(),
                request.endDate()
        );

        if (request.description() != null) {
            tour.updateDescription(request.description());
        }

        return tourRepository.save(tour).getId();
    }

    public void updateTour(UUID id, UpdateTourRequest request) {
        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tour not found: " + id));

        tour.updateDetails(
                request.name(),
                request.description(),
                request.startDate(),
                request.endDate()
        );
    }

    public void deleteTour(UUID id) {
        if (tourRepository.findById(id).isEmpty()) {
            throw new EntityNotFoundException("Tour not found: " + id);
        }
        tourRepository.deleteById(id);
    }

    public void addCost(UUID tourId, CreateCostRequest request) {

        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new EntityNotFoundException("Tour not found: " + tourId));


        Money money = new Money(request.amount(), request.currency());

        TourCost cost = new TourCost(
                request.title(),
                money,
                request.date()
        );

        tour.logCost(cost);

        tourRepository.save(tour);
    }

    public void updateCost(UUID tourId, UUID costId, UpdateCostRequest request) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new EntityNotFoundException("Tour not found: " + tourId));

        TourCost existingCost = tour.getCost(costId);
        Money updatedMoney = new Money(request.amount(), request.currency());
        existingCost.update(request.title(), updatedMoney, request.date());
    }

    public void deleteCost(UUID tourId, UUID costId) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new EntityNotFoundException("Tour not found: " + tourId));
        tour.removeCost(costId);
    }

    public void addRevenue(UUID tourId, CreateRevenueRequest request) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new EntityNotFoundException("Tour not found: " + tourId));

        Money money = new Money(request.amount(), request.currency());
        TourRevenue revenue = new TourRevenue(request.title(), money, request.date());
        tour.logRevenue(revenue);
        tourRepository.save(tour);
    }

    public void updateRevenue(UUID tourId, UUID revenueId, UpdateRevenueRequest request) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new EntityNotFoundException("Tour not found: " + tourId));

        TourRevenue existingRevenue = tour.getRevenue(revenueId);
        Money updatedMoney = new Money(request.amount(), request.currency());
        existingRevenue.update(request.title(), updatedMoney, request.date());
    }

    public void deleteRevenue(UUID tourId, UUID revenueId) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new EntityNotFoundException("Tour not found: " + tourId));
        tour.removeRevenue(revenueId);
    }

    @Transactional(readOnly = true) // Optymalizacja dla odczytu
    public List<TourResponse> getAllTours() {
        return tourRepository.findAll().stream()
                .map(t -> new TourResponse(t.getId(), t.getName(), t.getStartDate(), t.getEndDate()))
                .toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<TourResponse> getToursPage(int page, int size, String sortBy, String sortDir, String query) {
        String normalizedQuery = query == null ? "" : query.trim().toLowerCase();
        boolean descending = "desc".equalsIgnoreCase(sortDir);

        List<TourResponse> filtered = tourRepository.findAll().stream()
                .filter(tour -> normalizedQuery.isBlank()
                        || tour.getName().toLowerCase().contains(normalizedQuery)
                        || (tour.getDescription() != null && tour.getDescription().toLowerCase().contains(normalizedQuery)))
                .map(t -> new TourResponse(t.getId(), t.getName(), t.getStartDate(), t.getEndDate()))
                .sorted(resolveTourComparator(sortBy, descending))
                .toList();

        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);
        int fromIndex = safePage * safeSize;
        int toIndex = Math.min(fromIndex + safeSize, filtered.size());

        List<TourResponse> content = fromIndex >= filtered.size()
                ? List.of()
                : filtered.subList(fromIndex, toIndex);

        return PageResponse.of(content, safePage, safeSize, filtered.size(), sortBy, sortDir, query);
    }

    @Transactional(readOnly = true)
    public TourDetailResponse getTourDetails(UUID id) {
        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tour not found: " + id));


        var costResponses = tour.getCosts().stream()
                .map(c -> new TourCostResponse(
                        c.getId(),
                        c.getTitle(),
                        c.getCost().amount(),
                        c.getCost().currency(),
                        c.getCostDate()
                ))
                .toList();

        var revenueResponses = tour.getRevenues().stream()
                .map(r -> new TourRevenueResponse(
                        r.getId(),
                        r.getTitle(),
                        r.getRevenue().amount(),
                        r.getRevenue().currency(),
                        r.getRevenueDate()
                ))
                .toList();

        return new TourDetailResponse(
                tour.getId(),
                tour.getName(),
                tour.getDescription(),
                tour.getStartDate(),
                tour.getEndDate(),
                costResponses,
                revenueResponses
        );
    }

    @Transactional(readOnly = true)
    public TourProfitabilityResponse getProfitability(UUID tourId) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new EntityNotFoundException("Tour not found: " + tourId));

        BigDecimal totalCosts = tour.getCosts().stream()
                .map(cost -> cost.getCost().amount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal ticketRevenue = tourRepository.sumTicketSalesRevenue(tourId);
        BigDecimal manualRevenue = tour.getRevenues().stream()
                .map(revenue -> revenue.getRevenue().amount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalRevenue = ticketRevenue.add(manualRevenue);
        BigDecimal balance = totalRevenue.subtract(totalCosts);

        return new TourProfitabilityResponse(totalCosts, ticketRevenue, manualRevenue, totalRevenue, balance, "PLN");
    }

    private Comparator<TourResponse> resolveTourComparator(String sortBy, boolean descending) {
        Comparator<TourResponse> comparator = switch (sortBy) {
            case "endDate" -> Comparator.comparing(TourResponse::endDate);
            case "name" -> Comparator.comparing(TourResponse::name, String.CASE_INSENSITIVE_ORDER);
            case "startDate" -> Comparator.comparing(TourResponse::startDate);
            default -> Comparator.comparing(TourResponse::startDate);
        };

        return descending ? comparator.reversed() : comparator;
    }
}