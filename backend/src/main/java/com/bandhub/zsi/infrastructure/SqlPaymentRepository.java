package com.bandhub.zsi.infrastructure;

import com.bandhub.zsi.ecommerce.PaymentRepository;
import com.bandhub.zsi.ecommerce.domain.Payment;
import com.bandhub.zsi.shared.api.PagedResult;
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
class SqlPaymentRepository implements PaymentRepository {

    private final JpaPaymentRepository jpaRepository;

    SqlPaymentRepository(JpaPaymentRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Payment save(Payment payment) {
        return jpaRepository.save(payment);
    }

    @Override
    public Optional<Payment> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Payment> findByOrderId(UUID orderId) {
        return jpaRepository.findByOrder_IdOrderByCreatedAtDesc(orderId);
    }

    @Override
    public List<Payment> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public PagedResult<Payment> findPage(int page, int size, String sortBy, String sortDir, String q) {
        String pattern = (q == null || q.isBlank()) ? "%" : "%" + q.trim().toLowerCase() + "%";
        Sort.Direction dir = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        String prop = switch (sortBy) {
            case "status" -> "status";
            case "amount" -> "amount.amount";
            case "paidAt" -> "paidAt";
            default -> "createdAt";
        };
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), Sort.by(dir, prop));
        var result = jpaRepository.findAllFiltered(pattern, pageable);
        return new PagedResult<>(result.getContent(), result.getTotalElements());
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}

interface JpaPaymentRepository extends JpaRepository<Payment, UUID> {

    List<Payment> findByOrder_IdOrderByCreatedAtDesc(UUID orderId);

    @Query("SELECT p FROM Payment p JOIN p.order o WHERE LOWER(p.status) LIKE LOWER(:pattern) " +
            "OR LOWER(COALESCE(p.provider, '')) LIKE LOWER(:pattern)")
    org.springframework.data.domain.Page<Payment> findAllFiltered(@Param("pattern") String pattern, Pageable pageable);
}
