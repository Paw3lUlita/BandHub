package com.bandhub.zsi.ecommerce;

import com.bandhub.zsi.ecommerce.domain.Shipment;
import com.bandhub.zsi.shared.api.PagedResult;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ShipmentRepository {
    Shipment save(Shipment shipment);
    Optional<Shipment> findById(UUID id);
    List<Shipment> findAll();
    PagedResult<Shipment> findPage(int page, int size, String sortBy, String sortDir, String q);
    void deleteById(UUID id);
}
