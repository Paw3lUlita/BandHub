package com.bandhub.zsi.infrastructure;

import com.bandhub.zsi.shared.api.PagedResult;
import com.bandhub.zsi.ticketing.TicketOrderRepository;
import com.bandhub.zsi.ticketing.domain.TicketOrder;
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
    public PagedResult<TicketOrder> findPage(int page, int size, String sortBy, String sortDir, String q) {
        String pattern = (q == null || q.isBlank()) ? "%" : "%" + q.trim().toLowerCase() + "%";
        Sort.Direction dir = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        String prop = switch (sortBy) {
            case "status" -> "status";
            case "createdAt" -> "createdAt";
            default -> "userId";
        };
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), Sort.by(dir, prop));
        var result = jpaRepository.findAllFiltered(pattern, pageable);
        return new PagedResult<>(result.getContent(), result.getTotalElements());
    }

    @Override
    public void deleteById(UUID id) { jpaRepository.deleteById(id); }
}

interface JpaTicketOrderRepository extends JpaRepository<TicketOrder, UUID> {

    @Query("SELECT t FROM TicketOrder t WHERE LOWER(COALESCE(t.userId, '')) LIKE LOWER(:pattern) OR LOWER(COALESCE(t.status, '')) LIKE LOWER(:pattern)")
    org.springframework.data.domain.Page<TicketOrder> findAllFiltered(@Param("pattern") String pattern, Pageable pageable);
}
