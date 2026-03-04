package com.bandhub.zsi.infrastructure;

import com.bandhub.zsi.ecommerce.PaymentRepository;
import com.bandhub.zsi.ecommerce.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
class SqlPaymentRepository implements PaymentRepository {

    private final JpaPaymentRepository jpaRepository;

    SqlPaymentRepository(JpaPaymentRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Payment save(Payment payment) {
        return jpaRepository.save(payment);
    }

    @Override
    public Optional<Payment> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Payment> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}

interface JpaPaymentRepository extends JpaRepository<Payment, UUID> {}
