package com.bandhub.zsi.logistics;

import com.bandhub.zsi.logistics.domain.Tour;
import com.bandhub.zsi.logistics.domain.TourCost;
import com.bandhub.zsi.logistics.dto.*;
import com.bandhub.zsi.shared.Money;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional(readOnly = true) // Optymalizacja dla odczytu
    public List<TourResponse> getAllTours() {
        return tourRepository.findAll().stream()
                .map(t -> new TourResponse(t.getId(), t.getName(), t.getStartDate(), t.getEndDate()))
                .toList();
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

        return new TourDetailResponse(
                tour.getId(),
                tour.getName(),
                tour.getDescription(),
                tour.getStartDate(),
                tour.getEndDate(),
                costResponses
        );
    }
}