package com.bandhub.zsi.ecommerce;

import com.bandhub.zsi.ecommerce.domain.PaymentTransaction;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentTransactionRepository {
    PaymentTransaction save(PaymentTransaction paymentTransaction);
    Optional<PaymentTransaction> findById(UUID id);
    List<PaymentTransaction> findAll();
    void deleteById(UUID id);
}
