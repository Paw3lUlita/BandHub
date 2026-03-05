package com.bandhub.zsi.infrastructure;

import com.bandhub.zsi.ecommerce.ShipmentRepository;
import com.bandhub.zsi.ecommerce.domain.Shipment;
import com.bandhub.zsi.shared.api.PagedResult;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    public PagedResult<Shipment> findPage(int page, int size, String sortBy, String sortDir, String q) {
        String pattern = (q == null || q.isBlank()) ? "%" : "%" + q.trim().toLowerCase() + "%";
        Sort.Direction dir = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        String prop = switch (sortBy) {
            case "status" -> "status";
            case "carrier" -> "carrier";
            default -> "createdAt";
        };
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), Sort.by(dir, prop));
        var result = jpaRepository.findAllFiltered(pattern, pageable);
        return new PagedResult<>(result.getContent(), result.getTotalElements());
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}

interface JpaShipmentRepository extends JpaRepository<Shipment, UUID> {

    @Query("SELECT s FROM Shipment s WHERE LOWER(s.status) LIKE LOWER(:pattern) " +
            "OR LOWER(COALESCE(s.carrier, '')) LIKE LOWER(:pattern) " +
            "OR LOWER(COALESCE(s.trackingNumber, '')) LIKE LOWER(:pattern)")
    org.springframework.data.domain.Page<Shipment> findAllFiltered(@Param("pattern") String pattern, Pageable pageable);
}
