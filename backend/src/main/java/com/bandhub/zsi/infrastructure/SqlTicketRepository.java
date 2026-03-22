package com.bandhub.zsi.infrastructure;

import com.bandhub.zsi.ticketing.TicketRepository;
import com.bandhub.zsi.ticketing.domain.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
class SqlTicketRepository implements TicketRepository {

    private final JpaTicketEntityRepository jpaRepository;

    SqlTicketRepository(JpaTicketEntityRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Ticket save(Ticket ticket) {
        return jpaRepository.save(ticket);
    }

    @Override
    public Optional<Ticket> findById(UUID id) {
        return jpaRepository.findById(id);
    }
}

interface JpaTicketEntityRepository extends JpaRepository<Ticket, UUID> {
}
