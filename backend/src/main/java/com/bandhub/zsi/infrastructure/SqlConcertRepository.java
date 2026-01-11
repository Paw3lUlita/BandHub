package com.bandhub.zsi.infrastructure;

import com.bandhub.zsi.ticketing.ConcertRepository;
import com.bandhub.zsi.ticketing.domain.Concert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
class SqlConcertRepository implements ConcertRepository {

    private final JpaConcertRepository jpaRepository;

    SqlConcertRepository(JpaConcertRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Concert save(Concert concert) {
        return jpaRepository.save(concert);
    }

    @Override
    public Optional<Concert> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Concert> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}

interface JpaConcertRepository extends JpaRepository<Concert, UUID> {}