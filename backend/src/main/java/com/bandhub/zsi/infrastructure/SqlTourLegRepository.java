package com.bandhub.zsi.infrastructure;

import com.bandhub.zsi.logistics.TourLegRepository;
import com.bandhub.zsi.logistics.domain.TourLeg;
import com.bandhub.zsi.shared.api.PagedResult;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
class SqlTourLegRepository implements TourLegRepository {

    private final JpaTourLegRepository jpaRepository;

    SqlTourLegRepository(JpaTourLegRepository jpaRepository) { this.jpaRepository = jpaRepository; }
    @Override public TourLeg save(TourLeg tourLeg) { return jpaRepository.save(tourLeg); }
    @Override public Optional<TourLeg> findById(UUID id) { return jpaRepository.findById(id); }
    @Override public Optional<TourLeg> findByIdAndTour_Id(UUID legId, UUID tourId) { return jpaRepository.findByIdAndTour_Id(legId, tourId); }
    @Override public List<TourLeg> findAll() { return jpaRepository.findAll(); }
    @Override
    public PagedResult<TourLeg> findPage(int page, int size, String sortBy, String sortDir, String q) {
        String pattern = (q == null || q.isBlank()) ? "%" : "%" + q.trim().toLowerCase() + "%";
        Sort.Direction dir = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        String prop = switch (sortBy) {
            case "legOrder" -> "legOrder";
            case "legDate" -> "legDate";
            default -> "city";
        };
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), Sort.by(dir, prop));
        var result = jpaRepository.findAllFiltered(pattern, pageable);
        return new PagedResult<>(result.getContent(), result.getTotalElements());
    }
    @Override public void deleteById(UUID id) { jpaRepository.deleteById(id); }

    @Override
    public boolean existsByTour_IdAndConcertIdAndIdNot(UUID tourId, UUID concertId, UUID excludeLegId) {
        return jpaRepository.existsByTour_IdAndConcertIdAndIdNot(tourId, concertId, excludeLegId);
    }

    @Override
    public boolean existsByConcertIdAndTour_IdNot(UUID concertId, UUID tourId) {
        return jpaRepository.existsByConcertIdAndTour_IdNot(concertId, tourId);
    }
}

interface JpaTourLegRepository extends JpaRepository<TourLeg, UUID> {

    Optional<TourLeg> findByIdAndTour_Id(UUID id, UUID tourId);

    boolean existsByTour_IdAndConcertIdAndIdNot(UUID tourId, UUID concertId, UUID id);

    boolean existsByConcertIdAndTour_IdNot(UUID concertId, UUID tourId);

    @Query("SELECT t FROM TourLeg t WHERE LOWER(COALESCE(t.city, '')) LIKE LOWER(:pattern) OR LOWER(COALESCE(t.venueName, '')) LIKE LOWER(:pattern)")
    org.springframework.data.domain.Page<TourLeg> findAllFiltered(@Param("pattern") String pattern, Pageable pageable);
}
