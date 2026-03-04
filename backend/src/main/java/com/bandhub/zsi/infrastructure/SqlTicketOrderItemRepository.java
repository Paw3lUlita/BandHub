package com.bandhub.zsi.infrastructure;

import com.bandhub.zsi.ticketing.TicketOrderItemRepository;
import com.bandhub.zsi.ticketing.domain.TicketOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
class SqlTicketOrderItemRepository implements TicketOrderItemRepository {

    private final JpaTicketOrderItemRepository jpaRepository;

    SqlTicketOrderItemRepository(JpaTicketOrderItemRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public TicketOrderItem save(TicketOrderItem ticketOrderItem) { return jpaRepository.save(ticketOrderItem); }

    @Override
    public Optional<TicketOrderItem> findById(UUID id) { return jpaRepository.findById(id); }

    @Override
    public List<TicketOrderItem> findAll() { return jpaRepository.findAll(); }

    @Override
    public void deleteById(UUID id) { jpaRepository.deleteById(id); }
}

interface JpaTicketOrderItemRepository extends JpaRepository<TicketOrderItem, UUID> {}
