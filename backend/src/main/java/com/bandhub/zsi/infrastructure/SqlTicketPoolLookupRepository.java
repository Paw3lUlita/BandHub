package com.bandhub.zsi.infrastructure;

import com.bandhub.zsi.ticketing.TicketPoolLookupRepository;
import com.bandhub.zsi.ticketing.domain.TicketPool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
class SqlTicketPoolLookupRepository implements TicketPoolLookupRepository {

    private final JpaTicketPoolLookupRepository jpaRepository;

    SqlTicketPoolLookupRepository(JpaTicketPoolLookupRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<TicketPool> findById(UUID id) {
        return jpaRepository.findById(id);
    }
}

interface JpaTicketPoolLookupRepository extends JpaRepository<TicketPool, UUID> {}
