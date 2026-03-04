package com.bandhub.zsi.infrastructure;

import com.bandhub.zsi.logistics.TourRepository;
import com.bandhub.zsi.logistics.domain.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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

    @Query(value = """
            SELECT COALESCE(SUM((tp.total_quantity - tp.remaining_quantity) * tp.price), 0)
            FROM concerts c
            JOIN ticket_pools tp ON tp.concert_id = c.id
            WHERE c.tour_id = :tourId
            """, nativeQuery = true)
    BigDecimal sumTicketSalesRevenue(UUID tourId);
}