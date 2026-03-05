package com.bandhub.zsi.ecommerce;

import com.bandhub.zsi.ecommerce.domain.PaymentTransaction;
import com.bandhub.zsi.shared.api.PagedResult;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentTransactionRepository {
    PaymentTransaction save(PaymentTransaction paymentTransaction);
    Optional<PaymentTransaction> findById(UUID id);
    List<PaymentTransaction> findAll();
    PagedResult<PaymentTransaction> findPage(int page, int size, String sortBy, String sortDir, String q);
    void deleteById(UUID id);
}
