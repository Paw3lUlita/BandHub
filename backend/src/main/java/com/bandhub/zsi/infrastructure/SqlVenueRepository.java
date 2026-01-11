package com.bandhub.zsi.infrastructure;

import com.bandhub.zsi.ticketing.VenueRepository;
import com.bandhub.zsi.ticketing.domain.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
class SqlVenueRepository implements VenueRepository {

    private final JpaVenueRepository jpaRepository;

    SqlVenueRepository(JpaVenueRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Venue save(Venue venue) {
        return jpaRepository.save(venue);
    }

    @Override
    public List<Venue> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public Optional<Venue> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}

interface JpaVenueRepository extends JpaRepository<Venue, UUID> {}