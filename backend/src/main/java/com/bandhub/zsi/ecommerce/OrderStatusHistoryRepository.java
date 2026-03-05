package com.bandhub.zsi.ecommerce;

import com.bandhub.zsi.ecommerce.domain.OrderStatusHistory;
import com.bandhub.zsi.shared.api.PagedResult;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderStatusHistoryRepository {
    OrderStatusHistory save(OrderStatusHistory orderStatusHistory);
    Optional<OrderStatusHistory> findById(UUID id);
    List<OrderStatusHistory> findAll();
    PagedResult<OrderStatusHistory> findPage(int page, int size, String sortBy, String sortDir, String q);
    void deleteById(UUID id);
}
