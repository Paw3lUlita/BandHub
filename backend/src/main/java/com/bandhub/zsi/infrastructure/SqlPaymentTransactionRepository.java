package com.bandhub.zsi.infrastructure;

import com.bandhub.zsi.ecommerce.PaymentTransactionRepository;
import com.bandhub.zsi.ecommerce.domain.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
class SqlPaymentTransactionRepository implements PaymentTransactionRepository {

    private final JpaPaymentTransactionRepository jpaRepository;

    SqlPaymentTransactionRepository(JpaPaymentTransactionRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public PaymentTransaction save(PaymentTransaction paymentTransaction) {
        return jpaRepository.save(paymentTransaction);
    }

    @Override
    public Optional<PaymentTransaction> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<PaymentTransaction> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}

interface JpaPaymentTransactionRepository extends JpaRepository<PaymentTransaction, UUID> {}
