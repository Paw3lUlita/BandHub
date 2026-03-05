package com.bandhub.zsi.ecommerce;

import com.bandhub.zsi.ecommerce.domain.Payment;
import com.bandhub.zsi.shared.api.PagedResult;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository {
    Payment save(Payment payment);
    Optional<Payment> findById(UUID id);
    List<Payment> findAll();
    PagedResult<Payment> findPage(int page, int size, String sortBy, String sortDir, String q);
    void deleteById(UUID id);
}
