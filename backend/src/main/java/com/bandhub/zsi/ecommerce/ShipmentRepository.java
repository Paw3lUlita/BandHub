package com.bandhub.zsi.ecommerce;

import com.bandhub.zsi.ecommerce.domain.Shipment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ShipmentRepository {
    Shipment save(Shipment shipment);
    Optional<Shipment> findById(UUID id);
    List<Shipment> findAll();
    void deleteById(UUID id);
}
