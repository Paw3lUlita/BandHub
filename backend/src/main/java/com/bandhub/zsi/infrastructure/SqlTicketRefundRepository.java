package com.bandhub.zsi.infrastructure;

import com.bandhub.zsi.ticketing.TicketRefundRepository;
import com.bandhub.zsi.ticketing.domain.TicketRefund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
class SqlTicketRefundRepository implements TicketRefundRepository {

    private final JpaTicketRefundRepository jpaRepository;

    SqlTicketRefundRepository(JpaTicketRefundRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public TicketRefund save(TicketRefund ticketRefund) { return jpaRepository.save(ticketRefund); }

    @Override
    public Optional<TicketRefund> findById(UUID id) { return jpaRepository.findById(id); }

    @Override
    public List<TicketRefund> findAll() { return jpaRepository.findAll(); }

    @Override
    public void deleteById(UUID id) { jpaRepository.deleteById(id); }
}

interface JpaTicketRefundRepository extends JpaRepository<TicketRefund, UUID> {}
