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
        var result = shipmentRepository.findPage(page, size, sortBy, sortDir, query);
        List<ShipmentResponse> content = result.content().stream().map(this::toResponse).toList();
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);
        return PageResponse.of(content, safePage, safeSize, result.totalElements(), sortBy, sortDir, query);
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
