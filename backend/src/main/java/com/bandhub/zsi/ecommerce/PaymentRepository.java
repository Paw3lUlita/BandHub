package com.bandhub.zsi.ecommerce;

import com.bandhub.zsi.ecommerce.domain.Payment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository {
    Payment save(Payment payment);
    Optional<Payment> findById(UUID id);
    List<Payment> findAll();
    void deleteById(UUID id);
}
