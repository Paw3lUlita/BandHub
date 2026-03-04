package com.bandhub.zsi.ecommerce;

import com.bandhub.zsi.ecommerce.domain.Order;
import com.bandhub.zsi.ecommerce.dto.OrderDetailsResponse;
import com.bandhub.zsi.ecommerce.dto.OrderItemDto;
import com.bandhub.zsi.ecommerce.dto.OrderSummaryResponse;
import com.bandhub.zsi.ecommerce.domain.OrderStatus;
import com.bandhub.zsi.shared.api.PageResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class OrderAdminService {

    private final OrderRepository orderRepository;

    public OrderAdminService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional(readOnly = true)
    public List<OrderSummaryResponse> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toSummary)
                .toList();
    }

    public void updateOrderStatus(UUID orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + orderId));

        order.changeStatus(newStatus);
    }

    private OrderSummaryResponse toSummary(Order order) {
        var amount = order.getTotalAmount() != null ? order.getTotalAmount().amount() : BigDecimal.ZERO;
        var currency = order.getTotalAmount() != null ? order.getTotalAmount().currency() : "PLN";

        return new OrderSummaryResponse(
                order.getId(),
                order.getCreatedAt(),
                order.getStatus(),
                amount,
                currency,
                order.getUserId()
        );
    }

    @Transactional(readOnly = true)
    public OrderDetailsResponse getOrderDetails(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + orderId));

        List<OrderItemDto> itemsDto = order.getItems().stream()
                .map(item -> new OrderItemDto(
                        item.getProductId(),
                        item.getProductName(),
                        item.getQuantity(),
                        item.getUnitPrice().amount(),
                        item.calculateLineTotal().amount()
                ))
                .toList();

        return new OrderDetailsResponse(
                order.getId(),
                order.getCreatedAt(),
                order.getStatus(),
                order.getTotalAmount().amount(),
                order.getTotalAmount().currency(),
                order.getUserId(),
                itemsDto
        );
    }

    @Transactional(readOnly = true)
    public PageResponse<OrderSummaryResponse> getOrdersPage(
            int page,
            int size,
            String sortBy,
            String sortDir,
            String query,
            OrderStatus status
    ) {
        String normalizedQuery = query == null ? "" : query.trim().toLowerCase();
        boolean descending = "desc".equalsIgnoreCase(sortDir);

        List<OrderSummaryResponse> filtered = orderRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toSummary)
                .filter(order -> status == null || order.status() == status)
                .filter(order -> normalizedQuery.isBlank()
                        || order.id().toString().contains(normalizedQuery)
                        || order.userId().toLowerCase().contains(normalizedQuery))
                .sorted(resolveOrderComparator(sortBy, descending))
                .toList();

        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);
        int fromIndex = safePage * safeSize;
        int toIndex = Math.min(fromIndex + safeSize, filtered.size());

        List<OrderSummaryResponse> content = fromIndex >= filtered.size()
                ? List.of()
                : filtered.subList(fromIndex, toIndex);

        return PageResponse.of(content, safePage, safeSize, filtered.size(), sortBy, sortDir, query);
    }

    private Comparator<OrderSummaryResponse> resolveOrderComparator(String sortBy, boolean descending) {
        Comparator<OrderSummaryResponse> comparator = switch (sortBy) {
            case "totalAmount" -> Comparator.comparing(OrderSummaryResponse::totalAmount);
            case "status" -> Comparator.comparing(o -> o.status().name());
            case "userId" -> Comparator.comparing(OrderSummaryResponse::userId, String.CASE_INSENSITIVE_ORDER);
            case "createdAt" -> Comparator.comparing(OrderSummaryResponse::createdAt);
            default -> Comparator.comparing(OrderSummaryResponse::createdAt);
        };

        return descending ? comparator.reversed() : comparator;
    }
}