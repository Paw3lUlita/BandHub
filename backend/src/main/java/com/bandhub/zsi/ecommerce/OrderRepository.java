package com.bandhub.zsi.ecommerce;

import com.bandhub.zsi.ecommerce.domain.Order;
import java.util.List;

public interface OrderRepository {
    Order save(Order order);
    List<Order> findAllByOrderByCreatedAtDesc();
}