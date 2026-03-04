package com.bandhub.zsi.infrastructure;

import com.bandhub.zsi.logistics.TourLegRepository;
import com.bandhub.zsi.logistics.domain.TourLeg;
import org.springframework.data.jpa.repository.JpaRepository;
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
    @Override public List<TourLeg> findAll() { return jpaRepository.findAll(); }
    @Override public void deleteById(UUID id) { jpaRepository.deleteById(id); }
}

interface JpaTourLegRepository extends JpaRepository<TourLeg, UUID> {}
