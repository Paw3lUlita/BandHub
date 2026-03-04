package com.bandhub.zsi.infrastructure;

import com.bandhub.zsi.ecommerce.OrderStatusHistoryRepository;
import com.bandhub.zsi.ecommerce.domain.OrderStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
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
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}

interface JpaOrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory, UUID> {}
