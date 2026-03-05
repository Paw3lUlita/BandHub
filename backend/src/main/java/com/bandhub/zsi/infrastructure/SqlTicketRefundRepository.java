package com.bandhub.zsi.infrastructure;

import com.bandhub.zsi.shared.api.PagedResult;
import com.bandhub.zsi.ticketing.TicketRefundRepository;
import com.bandhub.zsi.ticketing.domain.TicketRefund;
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
    public PagedResult<TicketRefund> findPage(int page, int size, String sortBy, String sortDir, String q) {
        String pattern = (q == null || q.isBlank()) ? "%" : "%" + q.trim().toLowerCase() + "%";
        Sort.Direction dir = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        String prop = switch (sortBy) {
            case "status" -> "status";
            case "requestedAt" -> "requestedAt";
            default -> "status";
        };
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), Sort.by(dir, prop));
        var result = jpaRepository.findAllFiltered(pattern, pageable);
        return new PagedResult<>(result.getContent(), result.getTotalElements());
    }

    @Override
    public void deleteById(UUID id) { jpaRepository.deleteById(id); }
}

interface JpaTicketRefundRepository extends JpaRepository<TicketRefund, UUID> {

    @Query("SELECT t FROM TicketRefund t WHERE LOWER(COALESCE(t.status, '')) LIKE LOWER(:pattern)")
    org.springframework.data.domain.Page<TicketRefund> findAllFiltered(@Param("pattern") String pattern, Pageable pageable);
}
