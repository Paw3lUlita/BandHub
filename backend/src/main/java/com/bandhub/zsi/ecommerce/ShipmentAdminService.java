package com.bandhub.zsi.ecommerce;

import com.bandhub.zsi.ecommerce.domain.Order;
import com.bandhub.zsi.ecommerce.domain.Shipment;
import com.bandhub.zsi.ecommerce.dto.CreateShipmentRequest;
import com.bandhub.zsi.ecommerce.dto.ShipmentResponse;
import com.bandhub.zsi.ecommerce.dto.UpdateShipmentRequest;
import com.bandhub.zsi.shared.api.PageResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ShipmentAdminService {

    private final ShipmentRepository shipmentRepository;
    private final OrderRepository orderRepository;

    public ShipmentAdminService(ShipmentRepository shipmentRepository, OrderRepository orderRepository) {
        this.shipmentRepository = shipmentRepository;
        this.orderRepository = orderRepository;
    }

    public UUID create(CreateShipmentRequest request) {
        Order order = orderRepository.findById(request.orderId())
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + request.orderId()));

        Shipment shipment = Shipment.create(
                request.id(),
                order,
                request.carrier(),
                request.trackingNumber(),
                request.status(),
                request.shippedAt(),
                request.deliveredAt()
        );

        return shipmentRepository.save(shipment).getId();
    }

    public void update(UUID id, UpdateShipmentRequest request) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Shipment not found: " + id));
        shipment.update(
                request.carrier(),
                request.trackingNumber(),
                request.status(),
                request.shippedAt(),
                request.deliveredAt()
        );
    }

    public void delete(UUID id) {
        if (shipmentRepository.findById(id).isEmpty()) {
            throw new EntityNotFoundException("Shipment not found: " + id);
        }
        shipmentRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public ShipmentResponse getOne(UUID id) {
        return shipmentRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Shipment not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<ShipmentResponse> getAll() {
        return shipmentRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<ShipmentResponse> getPage(int page, int size, String sortBy, String sortDir, String query) {
        String normalizedQuery = query == null ? "" : query.trim().toLowerCase();
        boolean descending = "desc".equalsIgnoreCase(sortDir);

        List<ShipmentResponse> filtered = shipmentRepository.findAll().stream()
                .map(this::toResponse)
                .filter(item -> normalizedQuery.isBlank()
                        || item.status().toLowerCase().contains(normalizedQuery)
                        || (item.carrier() != null && item.carrier().toLowerCase().contains(normalizedQuery))
                        || (item.trackingNumber() != null && item.trackingNumber().toLowerCase().contains(normalizedQuery))
                        || item.orderId().toString().toLowerCase().contains(normalizedQuery))
                .sorted(resolveComparator(sortBy, descending))
                .toList();

        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);
        int fromIndex = safePage * safeSize;
        int toIndex = Math.min(fromIndex + safeSize, filtered.size());
        List<ShipmentResponse> content = fromIndex >= filtered.size()
                ? List.of()
                : filtered.subList(fromIndex, toIndex);

        return PageResponse.of(content, safePage, safeSize, filtered.size(), sortBy, sortDir, query);
    }

    private Comparator<ShipmentResponse> resolveComparator(String sortBy, boolean descending) {
        Comparator<ShipmentResponse> comparator = switch (sortBy) {
            case "status" -> Comparator.comparing(ShipmentResponse::status, String.CASE_INSENSITIVE_ORDER);
            case "carrier" -> Comparator.comparing(s -> s.carrier() == null ? "" : s.carrier(), String.CASE_INSENSITIVE_ORDER);
            case "createdAt" -> Comparator.comparing(ShipmentResponse::createdAt, Comparator.nullsLast(Comparator.naturalOrder()));
            default -> Comparator.comparing(ShipmentResponse::createdAt, Comparator.nullsLast(Comparator.naturalOrder()));
        };
        return descending ? comparator.reversed() : comparator;
    }

    private ShipmentResponse toResponse(Shipment shipment) {
        return new ShipmentResponse(
                shipment.getId(),
                shipment.getOrder().getId(),
                shipment.getCarrier(),
                shipment.getTrackingNumber(),
                shipment.getStatus(),
                shipment.getShippedAt(),
                shipment.getDeliveredAt(),
                shipment.getCreatedAt()
        );
    }
}
