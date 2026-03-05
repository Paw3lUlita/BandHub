package com.bandhub.zsi.infrastructure;

import com.bandhub.zsi.shared.api.PagedResult;
import com.bandhub.zsi.ticketing.TicketOrderItemRepository;
import com.bandhub.zsi.ticketing.domain.TicketOrderItem;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    public PagedResult<TicketOrderItem> findPage(int page, int size, String sortBy, String sortDir, String q) {
        String pattern = (q == null || q.isBlank()) ? "%" : "%" + q.trim().toLowerCase() + "%";
        Sort.Direction dir = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        String prop = switch (sortBy) {
            case "quantity" -> "quantity";
            case "ticketOrderId" -> "ticketOrder.id";
            default -> "ticketOrder.id";
        };
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), Sort.by(dir, prop));
        var result = jpaRepository.findAllFiltered(pattern, pageable);
        return new PagedResult<>(result.getContent(), result.getTotalElements());
    }

    @Override
    public void deleteById(UUID id) { jpaRepository.deleteById(id); }
}

interface JpaTicketOrderItemRepository extends JpaRepository<TicketOrderItem, UUID> {

    @Query("SELECT t FROM TicketOrderItem t JOIN t.ticketOrder o WHERE LOWER(CAST(o.id AS string)) LIKE LOWER(:pattern)")
    org.springframework.data.domain.Page<TicketOrderItem> findAllFiltered(@Param("pattern") String pattern, Pageable pageable);
}
