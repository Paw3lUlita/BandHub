package com.bandhub.zsi.infrastructure;

import com.bandhub.zsi.ticketing.TicketOrderRepository;
import com.bandhub.zsi.ticketing.domain.TicketOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
class SqlTicketOrderRepository implements TicketOrderRepository {

    private final JpaTicketOrderRepository jpaRepository;

    SqlTicketOrderRepository(JpaTicketOrderRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public TicketOrder save(TicketOrder ticketOrder) { return jpaRepository.save(ticketOrder); }

    @Override
    public Optional<TicketOrder> findById(UUID id) { return jpaRepository.findById(id); }

    @Override
    public List<TicketOrder> findAll() { return jpaRepository.findAll(); }

    @Override
    public void deleteById(UUID id) { jpaRepository.deleteById(id); }
}

interface JpaTicketOrderRepository extends JpaRepository<TicketOrder, UUID> {}
