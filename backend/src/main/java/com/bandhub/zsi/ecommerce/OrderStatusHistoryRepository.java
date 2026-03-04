package com.bandhub.zsi.ecommerce;

import com.bandhub.zsi.ecommerce.domain.OrderStatusHistory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderStatusHistoryRepository {
    OrderStatusHistory save(OrderStatusHistory orderStatusHistory);
    Optional<OrderStatusHistory> findById(UUID id);
    List<OrderStatusHistory> findAll();
    void deleteById(UUID id);
}
