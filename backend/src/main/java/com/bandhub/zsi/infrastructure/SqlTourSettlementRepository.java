package com.bandhub.zsi.infrastructure;

import com.bandhub.zsi.logistics.TourSettlementRepository;
import com.bandhub.zsi.logistics.domain.TourSettlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
class SqlTourSettlementRepository implements TourSettlementRepository {

    private final JpaTourSettlementRepository jpaRepository;

    SqlTourSettlementRepository(JpaTourSettlementRepository jpaRepository) { this.jpaRepository = jpaRepository; }
    @Override public TourSettlement save(TourSettlement settlement) { return jpaRepository.save(settlement); }
    @Override public Optional<TourSettlement> findById(UUID id) { return jpaRepository.findById(id); }
    @Override public List<TourSettlement> findAll() { return jpaRepository.findAll(); }
    @Override public void deleteById(UUID id) { jpaRepository.deleteById(id); }
}

interface JpaTourSettlementRepository extends JpaRepository<TourSettlement, UUID> {}
