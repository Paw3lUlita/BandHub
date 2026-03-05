package com.bandhub.zsi.ecommerce;

import com.bandhub.zsi.ecommerce.domain.Order;
import com.bandhub.zsi.ecommerce.domain.OrderStatusHistory;
import com.bandhub.zsi.ecommerce.dto.CreateOrderStatusHistoryRequest;
import com.bandhub.zsi.ecommerce.dto.OrderStatusHistoryResponse;
import com.bandhub.zsi.ecommerce.dto.UpdateOrderStatusHistoryRequest;
import com.bandhub.zsi.shared.api.PageResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class OrderStatusHistoryAdminService {

    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final OrderRepository orderRepository;

    public OrderStatusHistoryAdminService(OrderStatusHistoryRepository orderStatusHistoryRepository, OrderRepository orderRepository) {
        this.orderStatusHistoryRepository = orderStatusHistoryRepository;
        this.orderRepository = orderRepository;
    }

    public UUID create(CreateOrderStatusHistoryRequest request) {
        Order order = orderRepository.findById(request.orderId())
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + request.orderId()));

        OrderStatusHistory history = OrderStatusHistory.create(
                order,
                request.oldStatus(),
                request.newStatus(),
                request.changedBy(),
                request.note()
        );

        return orderStatusHistoryRepository.save(history).getId();
    }

    public void update(UUID id, UpdateOrderStatusHistoryRequest request) {
        OrderStatusHistory history = orderStatusHistoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order status history not found: " + id));
        history.update(
                request.oldStatus(),
                request.newStatus(),
                request.changedBy(),
                request.note()
        );
    }

    public void delete(UUID id) {
        if (orderStatusHistoryRepository.findById(id).isEmpty()) {
            throw new EntityNotFoundException("Order status history not found: " + id);
        }
        orderStatusHistoryRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public OrderStatusHistoryResponse getOne(UUID id) {
        return orderStatusHistoryRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Order status history not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<OrderStatusHistoryResponse> getAll() {
        return orderStatusHistoryRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<OrderStatusHistoryResponse> getPage(int page, int size, String sortBy, String sortDir, String query) {
        var result = orderStatusHistoryRepository.findPage(page, size, sortBy, sortDir, query);
        List<OrderStatusHistoryResponse> content = result.content().stream().map(this::toResponse).toList();
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);
        return PageResponse.of(content, safePage, safeSize, result.totalElements(), sortBy, sortDir, query);
    }

    private OrderStatusHistoryResponse toResponse(OrderStatusHistory history) {
        return new OrderStatusHistoryResponse(
                history.getId(),
                history.getOrder().getId(),
                history.getOldStatus(),
                history.getNewStatus(),
                history.getChangedBy(),
                history.getChangedAt(),
                history.getNote()
        );
    }
}
