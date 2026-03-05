package com.bandhub.zsi.ecommerce;

import com.bandhub.zsi.ecommerce.domain.Order;
import com.bandhub.zsi.ecommerce.domain.OrderStatus;
import com.bandhub.zsi.ecommerce.domain.OrderStatusHistory;
import com.bandhub.zsi.ecommerce.dto.OrderDetailsResponse;
import com.bandhub.zsi.ecommerce.dto.OrderItemDto;
import com.bandhub.zsi.ecommerce.dto.OrderSummaryResponse;
import com.bandhub.zsi.shared.api.PageResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class OrderAdminService {

    private static final Set<OrderStatus> TERMINAL = Set.of(OrderStatus.DELIVERED, OrderStatus.CANCELLED);

    private static final Set<OrderStatus> ALLOWED_FROM_NEW = Set.of(OrderStatus.PAID, OrderStatus.CANCELLED);
    private static final Set<OrderStatus> ALLOWED_FROM_PAID = Set.of(OrderStatus.SHIPPED, OrderStatus.CANCELLED);
    private static final Set<OrderStatus> ALLOWED_FROM_SHIPPED = Set.of(OrderStatus.DELIVERED);

    private final OrderRepository orderRepository;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final ProductRepository productRepository;
    private final PaymentRepository paymentRepository;
    private final ShipmentRepository shipmentRepository;

    public OrderAdminService(
            OrderRepository orderRepository,
            OrderStatusHistoryRepository orderStatusHistoryRepository,
            ProductRepository productRepository,
            PaymentRepository paymentRepository,
            ShipmentRepository shipmentRepository
    ) {
        this.orderRepository = orderRepository;
        this.orderStatusHistoryRepository = orderStatusHistoryRepository;
        this.productRepository = productRepository;
        this.paymentRepository = paymentRepository;
        this.shipmentRepository = shipmentRepository;
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

        OrderStatus current = order.getStatus();
        if (current == newStatus) {
            return;
        }
        if (TERMINAL.contains(current)) {
            throw new IllegalStateException("Cannot change status of order in terminal state: " + current);
        }
        if (!isAllowedTransition(current, newStatus)) {
            throw new IllegalStateException("Invalid transition from " + current + " to " + newStatus);
        }

        if (newStatus == OrderStatus.CANCELLED) {
            restoreStockForOrder(order);
        }

        String changedBy = getCurrentUserId();
        OrderStatusHistory history = OrderStatusHistory.create(
                order,
                current.name(),
                newStatus.name(),
                changedBy,
                null
        );
        orderStatusHistoryRepository.save(history);
        order.changeStatus(newStatus);
    }

    private void restoreStockForOrder(Order order) {
        for (var item : order.getItems()) {
            productRepository.findById(item.getProductId()).ifPresent(product ->
                    product.restoreStock(item.getQuantity())
            );
        }
    }

    private boolean isAllowedTransition(OrderStatus from, OrderStatus to) {
        return switch (from) {
            case NEW -> ALLOWED_FROM_NEW.contains(to);
            case PAID -> ALLOWED_FROM_PAID.contains(to);
            case SHIPPED -> ALLOWED_FROM_SHIPPED.contains(to);
            case DELIVERED, CANCELLED -> false;
        };
    }

    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "system";
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

        var payments = paymentRepository.findByOrderId(orderId);
        var shipments = shipmentRepository.findByOrderId(orderId);
        var history = orderStatusHistoryRepository.findByOrderIdOrderByChangedAtDesc(orderId);

        var payment = payments.isEmpty() ? null : toPaymentResponse(payments.getFirst());
        var shipment = shipments.isEmpty() ? null : toShipmentResponse(shipments.getFirst());
        var statusHistory = history.stream().map(this::toStatusHistoryResponse).toList();

        return new OrderDetailsResponse(
                order.getId(),
                order.getCreatedAt(),
                order.getStatus(),
                order.getTotalAmount().amount(),
                order.getTotalAmount().currency(),
                order.getUserId(),
                itemsDto,
                payment,
                shipment,
                statusHistory
        );
    }

    private com.bandhub.zsi.ecommerce.dto.PaymentResponse toPaymentResponse(com.bandhub.zsi.ecommerce.domain.Payment p) {
        return new com.bandhub.zsi.ecommerce.dto.PaymentResponse(
                p.getId(),
                p.getOrder().getId(),
                p.getProvider(),
                p.getProviderPaymentId(),
                p.getStatus(),
                p.getAmount().amount(),
                p.getAmount().currency(),
                p.getPaidAt(),
                p.getCreatedAt()
        );
    }

    private com.bandhub.zsi.ecommerce.dto.ShipmentResponse toShipmentResponse(com.bandhub.zsi.ecommerce.domain.Shipment s) {
        return new com.bandhub.zsi.ecommerce.dto.ShipmentResponse(
                s.getId(),
                s.getOrder().getId(),
                s.getCarrier(),
                s.getTrackingNumber(),
                s.getStatus(),
                s.getShippedAt(),
                s.getDeliveredAt(),
                s.getCreatedAt(),
                s.getDeliveryAddress()
        );
    }

    private com.bandhub.zsi.ecommerce.dto.OrderStatusHistoryResponse toStatusHistoryResponse(OrderStatusHistory h) {
        return new com.bandhub.zsi.ecommerce.dto.OrderStatusHistoryResponse(
                h.getId(),
                h.getOrder().getId(),
                h.getOldStatus(),
                h.getNewStatus(),
                h.getChangedBy(),
                h.getChangedAt(),
                h.getNote()
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