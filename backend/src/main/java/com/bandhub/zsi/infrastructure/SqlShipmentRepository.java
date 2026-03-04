package com.bandhub.zsi.infrastructure;

import com.bandhub.zsi.ecommerce.ShipmentRepository;
import com.bandhub.zsi.ecommerce.domain.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
class SqlShipmentRepository implements ShipmentRepository {

    private final JpaShipmentRepository jpaRepository;

    SqlShipmentRepository(JpaShipmentRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Shipment save(Shipment shipment) {
        return jpaRepository.save(shipment);
    }

    @Override
    public Optional<Shipment> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Shipment> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}

interface JpaShipmentRepository extends JpaRepository<Shipment, UUID> {}
