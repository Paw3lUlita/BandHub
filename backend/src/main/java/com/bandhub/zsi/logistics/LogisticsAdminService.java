package com.bandhub.zsi.logistics;

import com.bandhub.zsi.logistics.domain.Tour;
import com.bandhub.zsi.logistics.domain.TourCost;
import com.bandhub.zsi.logistics.domain.TourCostCategory;
import com.bandhub.zsi.logistics.domain.TourLeg;
import com.bandhub.zsi.logistics.domain.TourRevenue;
import com.bandhub.zsi.logistics.domain.TourRevenueCategory;
import com.bandhub.zsi.logistics.dto.*;
import com.bandhub.zsi.shared.api.PageResponse;
import com.bandhub.zsi.shared.Money;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional
public class LogisticsAdminService {

    private final TourRepository tourRepository;
    private final TourCostCategoryRepository costCategoryRepository;
    private final TourRevenueCategoryRepository revenueCategoryRepository;
    private final TourLegRepository tourLegRepository;

    public LogisticsAdminService(
            TourRepository tourRepository,
            TourCostCategoryRepository costCategoryRepository,
            TourRevenueCategoryRepository revenueCategoryRepository,
            TourLegRepository tourLegRepository
    ) {
        this.tourRepository = tourRepository;
        this.costCategoryRepository = costCategoryRepository;
        this.revenueCategoryRepository = revenueCategoryRepository;
        this.tourLegRepository = tourLegRepository;
    }

    public UUID createTour(CreateTourRequest request) {
        validateTourDateRange(request.startDate(), request.endDate());
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

        validateTourDateRange(request.startDate(), request.endDate());
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

        validateMovementDateWithinTour(tour, request.date());
        TourCostCategory category = resolveCostCategory(request.costCategoryId());
        TourLeg leg = resolveTourLeg(tourId, request.tourLegId());
        if (leg != null) {
            validateLegDateWithinTour(tour, leg.getLegDate());
        }

        Money money = new Money(request.amount(), request.currency());
        assertLegBudget(tour, leg, money, null);

        TourCost cost = new TourCost(
                request.title(),
                money,
                request.date(),
                category,
                leg
        );

        tour.logCost(cost);
        tourRepository.save(tour);
    }

    public void updateCost(UUID tourId, UUID costId, UpdateCostRequest request) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new EntityNotFoundException("Tour not found: " + tourId));

        validateMovementDateWithinTour(tour, request.date());
        TourCostCategory category = resolveCostCategory(request.costCategoryId());
        TourLeg leg = resolveTourLeg(tourId, request.tourLegId());
        if (leg != null) {
            validateLegDateWithinTour(tour, leg.getLegDate());
        }

        TourCost existingCost = tour.getCost(costId);
        Money updatedMoney = new Money(request.amount(), request.currency());
        assertLegBudget(tour, leg, updatedMoney, costId);

        existingCost.update(request.title(), updatedMoney, request.date(), category, leg);
    }

    public void deleteCost(UUID tourId, UUID costId) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new EntityNotFoundException("Tour not found: " + tourId));
        tour.removeCost(costId);
    }

    public void addRevenue(UUID tourId, CreateRevenueRequest request) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new EntityNotFoundException("Tour not found: " + tourId));

        validateMovementDateWithinTour(tour, request.date());
        TourRevenueCategory category = resolveRevenueCategory(request.revenueCategoryId());
        TourLeg leg = resolveTourLeg(tourId, request.tourLegId());
        if (leg != null) {
            validateLegDateWithinTour(tour, leg.getLegDate());
        }

        Money money = new Money(request.amount(), request.currency());
        TourRevenue revenue = new TourRevenue(request.title(), money, request.date(), category, leg);
        tour.logRevenue(revenue);
        tourRepository.save(tour);
    }

    public void updateRevenue(UUID tourId, UUID revenueId, UpdateRevenueRequest request) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new EntityNotFoundException("Tour not found: " + tourId));

        validateMovementDateWithinTour(tour, request.date());
        TourRevenueCategory category = resolveRevenueCategory(request.revenueCategoryId());
        TourLeg leg = resolveTourLeg(tourId, request.tourLegId());
        if (leg != null) {
            validateLegDateWithinTour(tour, leg.getLegDate());
        }

        TourRevenue existingRevenue = tour.getRevenue(revenueId);
        Money updatedMoney = new Money(request.amount(), request.currency());
        existingRevenue.update(request.title(), updatedMoney, request.date(), category, leg);
    }

    public void deleteRevenue(UUID tourId, UUID revenueId) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new EntityNotFoundException("Tour not found: " + tourId));
        tour.removeRevenue(revenueId);
    }

    @Transactional(readOnly = true)
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
        Tour tour = tourRepository.findWithDetailsById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tour not found: " + id));

        var costResponses = tour.getCosts().stream()
                .map(c -> new TourCostResponse(
                        c.getId(),
                        c.getTitle(),
                        c.getCost().amount(),
                        c.getCost().currency(),
                        c.getCostDate(),
                        c.getCostCategory() != null ? c.getCostCategory().getId() : null,
                        c.getCostCategory() != null ? c.getCostCategory().getName() : null,
                        c.getTourLeg() != null ? c.getTourLeg().getId() : null
                ))
                .toList();

        var revenueResponses = tour.getRevenues().stream()
                .map(r -> new TourRevenueResponse(
                        r.getId(),
                        r.getTitle(),
                        r.getRevenue().amount(),
                        r.getRevenue().currency(),
                        r.getRevenueDate(),
                        r.getRevenueCategory() != null ? r.getRevenueCategory().getId() : null,
                        r.getRevenueCategory() != null ? r.getRevenueCategory().getName() : null,
                        r.getTourLeg() != null ? r.getTourLeg().getId() : null
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

    private static void validateTourDateRange(LocalDateTime start, LocalDateTime end) {
        if (start != null && end != null && end.isBefore(start)) {
            throw new IllegalArgumentException("Tour end date must not be before start date");
        }
    }

    private static void validateMovementDateWithinTour(Tour tour, LocalDateTime at) {
        if (at == null) {
            return;
        }
        LocalDateTime start = tour.getStartDate();
        LocalDateTime end = tour.getEndDate();
        if (start != null && at.isBefore(start)) {
            throw new IllegalArgumentException("Entry date is before tour start");
        }
        if (end != null && at.isAfter(end)) {
            throw new IllegalArgumentException("Entry date is after tour end");
        }
    }

    private static void validateLegDateWithinTour(Tour tour, LocalDateTime legDate) {
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

    private TourCostCategory resolveCostCategory(UUID id) {
        if (id == null) {
            return null;
        }
        TourCostCategory c = costCategoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cost category not found: " + id));
        if (!c.isActive()) {
            throw new IllegalArgumentException("Cost category is inactive: " + id);
        }
        return c;
    }

    private TourRevenueCategory resolveRevenueCategory(UUID id) {
        if (id == null) {
            return null;
        }
        TourRevenueCategory c = revenueCategoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Revenue category not found: " + id));
        if (!c.isActive()) {
            throw new IllegalArgumentException("Revenue category is inactive: " + id);
        }
        return c;
    }

    private TourLeg resolveTourLeg(UUID tourId, UUID legId) {
        if (legId == null) {
            return null;
        }
        return tourLegRepository.findByIdAndTour_Id(legId, tourId)
                .orElseThrow(() -> new IllegalArgumentException("Tour leg does not belong to this tour: " + legId));
    }

    private static void assertLegBudget(Tour tour, TourLeg leg, Money newMoney, UUID excludeCostId) {
        if (leg == null || leg.getPlannedBudget() == null) {
            return;
        }
        String legCur = leg.getCurrency() != null && !leg.getCurrency().isBlank() ? leg.getCurrency() : "PLN";
        if (!legCur.equals(newMoney.currency())) {
            return;
        }
        BigDecimal sum = tour.getCosts().stream()
                .filter(c -> c.getTourLeg() != null && leg.getId().equals(c.getTourLeg().getId()))
                .filter(c -> excludeCostId == null || !excludeCostId.equals(c.getId()))
                .filter(c -> legCur.equals(c.getCost().currency()))
                .map(c -> c.getCost().amount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        sum = sum.add(newMoney.amount());
        if (sum.compareTo(leg.getPlannedBudget()) > 0) {
            throw new IllegalStateException("Total costs on this leg exceed planned budget for the leg");
        }
    }

    private Comparator<TourResponse> resolveTourComparator(String sortBy, boolean descending) {
        Comparator<TourResponse> comparator = switch (Objects.requireNonNullElse(sortBy, "startDate")) {
            case "endDate" -> Comparator.comparing(TourResponse::endDate, Comparator.nullsLast(Comparator.naturalOrder()));
            case "name" -> Comparator.comparing(TourResponse::name, String.CASE_INSENSITIVE_ORDER);
            case "startDate" -> Comparator.comparing(TourResponse::startDate, Comparator.nullsLast(Comparator.naturalOrder()));
            default -> Comparator.comparing(TourResponse::startDate, Comparator.nullsLast(Comparator.naturalOrder()));
        };

        return descending ? comparator.reversed() : comparator;
    }
}
