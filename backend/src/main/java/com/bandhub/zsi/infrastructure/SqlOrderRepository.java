package com.bandhub.zsi.infrastructure; // Zgodnie z Twoją uwagą

import com.bandhub.zsi.ecommerce.OrderRepository; // Importujemy Port
import com.bandhub.zsi.ecommerce.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
class SqlOrderRepository implements OrderRepository {

    private final JpaOrderRepository jpaRepository;

    SqlOrderRepository(JpaOrderRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Order save(Order order) {
        return jpaRepository.save(order);
    }

    @Override
    public List<Order> findAllByOrderByCreatedAtDesc() {
        return jpaRepository.findAllByOrderByCreatedAtDesc();
    }
}

interface JpaOrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findAllByOrderByCreatedAtDesc();
}
