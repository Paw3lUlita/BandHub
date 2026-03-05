package com.bandhub.zsi.ecommerce;

import com.bandhub.zsi.ecommerce.domain.Order;
import com.bandhub.zsi.ecommerce.domain.OrderStatus;
import com.bandhub.zsi.ecommerce.dto.MerchSalesSnapshotResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MerchReportService {

    private final OrderRepository orderRepository;

    public MerchReportService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional(readOnly = true)
    public MerchSalesSnapshotResponse getMerchSalesSnapshot(LocalDateTime from, LocalDateTime to) {
        List<Order> orders = orderRepository.findAllByOrderByCreatedAtDesc().stream()
                .filter(o -> !OrderStatus.CANCELLED.equals(o.getStatus()))
                .filter(o -> isInRange(o.getCreatedAt(), from, to))
                .toList();

        long orderCount = orders.size();
        BigDecimal totalRevenue = orders.stream()
                .map(o -> o.getTotalAmount() != null ? o.getTotalAmount().amount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        String currency = orders.stream()
                .filter(o -> o.getTotalAmount() != null)
                .findFirst()
                .map(o -> o.getTotalAmount().currency())
                .orElse("PLN");
        long totalUnits = orders.stream()
                .flatMapToInt(o -> o.getItems().stream().mapToInt(item -> item.getQuantity()))
                .sum();

        return new MerchSalesSnapshotResponse(orderCount, totalRevenue, currency, totalUnits);
    }

    private boolean isInRange(LocalDateTime value, LocalDateTime from, LocalDateTime to) {
        if (from != null && value.isBefore(from)) return false;
        if (to != null && value.isAfter(to)) return false;
        return true;
    }
}
