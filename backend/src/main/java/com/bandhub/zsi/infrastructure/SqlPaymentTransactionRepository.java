package com.bandhub.zsi.infrastructure;

import com.bandhub.zsi.ecommerce.PaymentTransactionRepository;
import com.bandhub.zsi.ecommerce.domain.PaymentTransaction;
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
class SqlPaymentTransactionRepository implements PaymentTransactionRepository {

    private final JpaPaymentTransactionRepository jpaRepository;

    SqlPaymentTransactionRepository(JpaPaymentTransactionRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public PaymentTransaction save(PaymentTransaction paymentTransaction) {
        return jpaRepository.save(paymentTransaction);
    }

    @Override
    public Optional<PaymentTransaction> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<PaymentTransaction> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public PagedResult<PaymentTransaction> findPage(int page, int size, String sortBy, String sortDir, String q) {
        String pattern = (q == null || q.isBlank()) ? "%" : "%" + q.trim().toLowerCase() + "%";
        Sort.Direction dir = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        String prop = "eventType".equals(sortBy) ? "eventType" : "createdAt";
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), Sort.by(dir, prop));
        var result = jpaRepository.findAllFiltered(pattern, pageable);
        return new PagedResult<>(result.getContent(), result.getTotalElements());
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}

interface JpaPaymentTransactionRepository extends JpaRepository<PaymentTransaction, UUID> {

    @Query("SELECT pt FROM PaymentTransaction pt WHERE LOWER(pt.eventType) LIKE LOWER(:pattern) " +
            "OR LOWER(COALESCE(pt.externalStatus, '')) LIKE LOWER(:pattern)")
    org.springframework.data.domain.Page<PaymentTransaction> findAllFiltered(@Param("pattern") String pattern, Pageable pageable);
}
