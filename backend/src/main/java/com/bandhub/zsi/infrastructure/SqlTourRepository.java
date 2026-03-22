package com.bandhub.zsi.infrastructure;

import com.bandhub.zsi.logistics.TourRepository;
import com.bandhub.zsi.logistics.domain.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
class SqlTourRepository implements TourRepository {

    private final JpaTourRepository jpaRepository;

    SqlTourRepository(JpaTourRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Tour save(Tour tour) {
        return jpaRepository.save(tour);
    }

    @Override
    public Optional<Tour> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<Tour> findWithDetailsById(UUID id) {
        Optional<Tour> withCosts = jpaRepository.findByIdWithCostDetails(id);
        if (withCosts.isEmpty()) {
            return Optional.empty();
        }
        // Drugie zapytanie: osobno revenues — unikamy MultipleBagFetchException (dwa @OneToMany w jednym fetch).
        jpaRepository.findByIdWithRevenueDetails(id);
        return withCosts;
    }

    @Override
    public List<Tour> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public BigDecimal sumTicketSalesRevenue(UUID tourId) {
        return jpaRepository.sumTicketSalesRevenue(tourId);
    }
}

interface JpaTourRepository extends JpaRepository<Tour, UUID> {

    @Query("""
            SELECT DISTINCT t FROM Tour t
            LEFT JOIN FETCH t.costs c
            LEFT JOIN FETCH c.costCategory
            LEFT JOIN FETCH c.tourLeg
            WHERE t.id = :id
            """)
    Optional<Tour> findByIdWithCostDetails(@Param("id") UUID id);

    @Query("""
            SELECT DISTINCT t FROM Tour t
            LEFT JOIN FETCH t.revenues r
            LEFT JOIN FETCH r.revenueCategory
            LEFT JOIN FETCH r.tourLeg
            WHERE t.id = :id
            """)
    Optional<Tour> findByIdWithRevenueDetails(@Param("id") UUID id);

    @Query(value = """
            SELECT COALESCE(SUM(to2.total_amount), 0)
            FROM concerts c
            JOIN ticket_orders to2 ON to2.concert_id = c.id
            WHERE c.tour_id = :tourId
              AND to2.status <> 'CANCELLED'
            """, nativeQuery = true)
    BigDecimal sumTicketSalesRevenue(UUID tourId);
}