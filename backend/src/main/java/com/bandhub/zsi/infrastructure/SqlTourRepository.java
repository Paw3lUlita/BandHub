package com.bandhub.zsi.infrastructure;

import com.bandhub.zsi.logistics.TourRepository;
import com.bandhub.zsi.logistics.domain.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
}

interface JpaTourRepository extends JpaRepository<Tour, UUID> {}