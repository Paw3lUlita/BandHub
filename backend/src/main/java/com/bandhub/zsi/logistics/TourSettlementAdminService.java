package com.bandhub.zsi.logistics;

import com.bandhub.zsi.logistics.domain.Tour;
import com.bandhub.zsi.logistics.domain.TourSettlement;
import com.bandhub.zsi.logistics.dto.CreateTourSettlementRequest;
import com.bandhub.zsi.logistics.dto.TourSettlementResponse;
import com.bandhub.zsi.logistics.dto.UpdateTourSettlementRequest;
import com.bandhub.zsi.shared.api.PageResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class TourSettlementAdminService {

    private final TourSettlementRepository settlementRepository;
    private final TourRepository tourRepository;
    private final JdbcTemplate jdbcTemplate;

    public TourSettlementAdminService(
            TourSettlementRepository settlementRepository,
            TourRepository tourRepository,
            JdbcTemplate jdbcTemplate
    ) {
        this.settlementRepository = settlementRepository;
        this.tourRepository = tourRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    public UUID create(CreateTourSettlementRequest request) {
        Tour tour = tourRepository.findById(request.tourId()).orElseThrow(() -> new EntityNotFoundException("Tour not found: " + request.tourId()));
        TourSettlement settlement = TourSettlement.create(tour, request.settledBy(), request.settledAt(), request.totalCosts(), request.totalRevenue(), request.balance(), request.currency(), request.notes());
        return settlementRepository.save(settlement).getId();
    }

    public void update(UUID id, UpdateTourSettlementRequest request) {
        TourSettlement settlement = settlementRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Tour settlement not found: " + id));
        Tour tour = tourRepository.findById(request.tourId()).orElseThrow(() -> new EntityNotFoundException("Tour not found: " + request.tourId()));
        settlement.update(tour, request.settledBy(), request.settledAt(), request.totalCosts(), request.totalRevenue(), request.balance(), request.currency(), request.notes());
    }

    public void delete(UUID id) { if (settlementRepository.findById(id).isEmpty()) throw new EntityNotFoundException("Tour settlement not found: " + id); settlementRepository.deleteById(id); }

    /**
     * Recomputes totals from {@code tour_costs}, {@code tour_revenues} and ticket order amounts (same rules as {@code fn_close_tour_settlement}).
     */
    public TourSettlementResponse closeFromComputedData(UUID tourId, String settledBy, String notes) {
        tourRepository.findById(tourId).orElseThrow(() -> new EntityNotFoundException("Tour not found: " + tourId));
        String actor = settledBy != null && !settledBy.isBlank() ? settledBy : "system";
        jdbcTemplate.query(
                "SELECT fn_close_tour_settlement(?::uuid, ?)",
                ps -> {
                    ps.setObject(1, tourId);
                    ps.setString(2, actor);
                },
                (rs, rowNum) -> null
        );
        TourSettlement settlement = settlementRepository.findByTour_Id(tourId)
                .orElseThrow(() -> new IllegalStateException("Settlement row missing after close"));
        if (notes != null && !notes.isBlank()) {
            settlement.mergeNotes(notes);
            settlementRepository.save(settlement);
        }
        return toResponse(settlement);
    }

    @Transactional(readOnly = true)
    public Optional<TourSettlementResponse> findByTourId(UUID tourId) {
        return settlementRepository.findByTour_Id(tourId).map(this::toResponse);
    }

    @Transactional(readOnly = true) public TourSettlementResponse getOne(UUID id) { return settlementRepository.findById(id).map(this::toResponse).orElseThrow(() -> new EntityNotFoundException("Tour settlement not found: " + id)); }
    @Transactional(readOnly = true) public List<TourSettlementResponse> getAll() { return settlementRepository.findAll().stream().map(this::toResponse).toList(); }
    @Transactional(readOnly = true) public PageResponse<TourSettlementResponse> getPage(int page, int size, String sortBy, String sortDir, String query) {
        var result = settlementRepository.findPage(page, size, sortBy, sortDir, query);
        List<TourSettlementResponse> content = result.content().stream().map(this::toResponse).toList();
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);
        return PageResponse.of(content, safePage, safeSize, result.totalElements(), sortBy, sortDir, query);
    }
    private TourSettlementResponse toResponse(TourSettlement s) {
        Tour tour = s.getTour();
        return new TourSettlementResponse(
                s.getId(),
                tour.getId(),
                tour.getName(),
                s.getSettledBy(),
                s.getSettledAt(),
                s.getTotalCosts(),
                s.getTotalRevenue(),
                s.getBalance(),
                s.getCurrency(),
                s.getNotes()
        );
    }
}
