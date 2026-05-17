package com.bandhub.zsi.ecommerce;

import com.bandhub.zsi.ecommerce.domain.Order;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {
    Order save(Order order);
    List<Order> findAllByOrderByCreatedAtDesc();

    List<Order> findByUserIdOrderByCreatedAtDesc(String userId);

    Optional<Order> findById(UUID orderId);
}