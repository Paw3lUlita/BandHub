package com.bandhub.zsi.infrastructure;

import com.bandhub.zsi.shared.api.PagedResult;
import com.bandhub.zsi.ticketing.TicketValidationRepository;
import com.bandhub.zsi.ticketing.domain.TicketValidation;
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
class SqlTicketValidationRepository implements TicketValidationRepository {

    private final JpaTicketValidationRepository jpaRepository;

    SqlTicketValidationRepository(JpaTicketValidationRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public TicketValidation save(TicketValidation ticketValidation) { return jpaRepository.save(ticketValidation); }

    @Override
    public Optional<TicketValidation> findById(UUID id) { return jpaRepository.findById(id); }

    @Override
    public List<TicketValidation> findAll() { return jpaRepository.findAll(); }

    @Override
    public PagedResult<TicketValidation> findPage(int page, int size, String sortBy, String sortDir, String q) {
        String pattern = (q == null || q.isBlank()) ? "%" : "%" + q.trim().toLowerCase() + "%";
        Sort.Direction dir = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        String prop = switch (sortBy) {
            case "validationResult" -> "validationResult";
            case "validationTime" -> "validationTime";
            default -> "validationResult";
        };
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), Sort.by(dir, prop));
        var result = jpaRepository.findAllFiltered(pattern, pageable);
        return new PagedResult<>(result.getContent(), result.getTotalElements());
    }

    @Override
    public void deleteById(UUID id) { jpaRepository.deleteById(id); }
}

interface JpaTicketValidationRepository extends JpaRepository<TicketValidation, UUID> {

    @Query("SELECT t FROM TicketValidation t WHERE LOWER(COALESCE(t.validationResult, '')) LIKE LOWER(:pattern)")
    org.springframework.data.domain.Page<TicketValidation> findAllFiltered(@Param("pattern") String pattern, Pageable pageable);
}
