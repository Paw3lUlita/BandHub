package com.bandhub.zsi.infrastructure;

import com.bandhub.zsi.ecommerce.OrderStatusHistoryRepository;
import com.bandhub.zsi.ecommerce.domain.OrderStatusHistory;
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
class SqlOrderStatusHistoryRepository implements OrderStatusHistoryRepository {

    private final JpaOrderStatusHistoryRepository jpaRepository;

    SqlOrderStatusHistoryRepository(JpaOrderStatusHistoryRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public OrderStatusHistory save(OrderStatusHistory orderStatusHistory) {
        return jpaRepository.save(orderStatusHistory);
    }

    @Override
    public Optional<OrderStatusHistory> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<OrderStatusHistory> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public PagedResult<OrderStatusHistory> findPage(int page, int size, String sortBy, String sortDir, String q) {
        String pattern = (q == null || q.isBlank()) ? "%" : "%" + q.trim().toLowerCase() + "%";
        Sort.Direction dir = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        String prop = "newStatus".equals(sortBy) ? "newStatus" : "changedAt";
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), Sort.by(dir, prop));
        var result = jpaRepository.findAllFiltered(pattern, pageable);
        return new PagedResult<>(result.getContent(), result.getTotalElements());
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}

interface JpaOrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory, UUID> {

    @Query("SELECT osh FROM OrderStatusHistory osh WHERE LOWER(osh.newStatus) LIKE LOWER(:pattern) " +
            "OR LOWER(COALESCE(osh.oldStatus, '')) LIKE LOWER(:pattern) " +
            "OR LOWER(COALESCE(osh.changedBy, '')) LIKE LOWER(:pattern)")
    org.springframework.data.domain.Page<OrderStatusHistory> findAllFiltered(@Param("pattern") String pattern, Pageable pageable);
}
