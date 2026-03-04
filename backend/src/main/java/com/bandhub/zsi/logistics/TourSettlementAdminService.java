package com.bandhub.zsi.logistics;

import com.bandhub.zsi.logistics.domain.Tour;
import com.bandhub.zsi.logistics.domain.TourSettlement;
import com.bandhub.zsi.logistics.dto.CreateTourSettlementRequest;
import com.bandhub.zsi.logistics.dto.TourSettlementResponse;
import com.bandhub.zsi.logistics.dto.UpdateTourSettlementRequest;
import com.bandhub.zsi.shared.api.PageResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class TourSettlementAdminService {

    private final TourSettlementRepository settlementRepository;
    private final TourRepository tourRepository;

    public TourSettlementAdminService(TourSettlementRepository settlementRepository, TourRepository tourRepository) {
        this.settlementRepository = settlementRepository;
        this.tourRepository = tourRepository;
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
    @Transactional(readOnly = true) public TourSettlementResponse getOne(UUID id) { return settlementRepository.findById(id).map(this::toResponse).orElseThrow(() -> new EntityNotFoundException("Tour settlement not found: " + id)); }
    @Transactional(readOnly = true) public List<TourSettlementResponse> getAll() { return settlementRepository.findAll().stream().map(this::toResponse).toList(); }
    @Transactional(readOnly = true) public PageResponse<TourSettlementResponse> getPage(int page, int size, String sortBy, String sortDir, String query) { String normalized = query == null ? "" : query.trim().toLowerCase(); boolean desc = "desc".equalsIgnoreCase(sortDir); List<TourSettlementResponse> filtered = settlementRepository.findAll().stream().map(this::toResponse).filter(s -> normalized.isBlank() || (s.settledBy()!=null && s.settledBy().toLowerCase().contains(normalized)) || s.tourId().toString().toLowerCase().contains(normalized)).sorted(resolveComparator(sortBy, desc)).toList(); int safePage=Math.max(page,0); int safeSize=Math.max(size,1); int from=safePage*safeSize; int to=Math.min(from+safeSize,filtered.size()); List<TourSettlementResponse> content=from>=filtered.size()?List.of():filtered.subList(from,to); return PageResponse.of(content,safePage,safeSize,filtered.size(),sortBy,sortDir,query); }
    private Comparator<TourSettlementResponse> resolveComparator(String sortBy, boolean desc){ Comparator<TourSettlementResponse> c = switch(sortBy){ case "settledAt" -> Comparator.comparing(TourSettlementResponse::settledAt, Comparator.nullsLast(Comparator.naturalOrder())); case "balance" -> Comparator.comparing(TourSettlementResponse::balance, Comparator.nullsLast(Comparator.naturalOrder())); default -> Comparator.comparing(TourSettlementResponse::settledAt, Comparator.nullsLast(Comparator.naturalOrder()));}; return desc?c.reversed():c;}
    private TourSettlementResponse toResponse(TourSettlement s){ return new TourSettlementResponse(s.getId(), s.getTour().getId(), s.getSettledBy(), s.getSettledAt(), s.getTotalCosts(), s.getTotalRevenue(), s.getBalance(), s.getCurrency(), s.getNotes());}
}
