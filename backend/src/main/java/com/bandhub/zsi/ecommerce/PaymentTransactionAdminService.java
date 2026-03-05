package com.bandhub.zsi.ecommerce;

import com.bandhub.zsi.ecommerce.domain.Payment;
import com.bandhub.zsi.ecommerce.domain.PaymentTransaction;
import com.bandhub.zsi.ecommerce.dto.CreatePaymentTransactionRequest;
import com.bandhub.zsi.ecommerce.dto.PaymentTransactionResponse;
import com.bandhub.zsi.ecommerce.dto.UpdatePaymentTransactionRequest;
import com.bandhub.zsi.shared.api.PageResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PaymentTransactionAdminService {

    private final PaymentTransactionRepository paymentTransactionRepository;
    private final PaymentRepository paymentRepository;

    public PaymentTransactionAdminService(PaymentTransactionRepository paymentTransactionRepository, PaymentRepository paymentRepository) {
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.paymentRepository = paymentRepository;
    }

    public UUID create(CreatePaymentTransactionRequest request) {
        Payment payment = paymentRepository.findById(request.paymentId())
                .orElseThrow(() -> new EntityNotFoundException("Payment not found: " + request.paymentId()));

        PaymentTransaction transaction = PaymentTransaction.create(
                payment,
                request.eventType(),
                request.externalStatus(),
                request.payload()
        );

        return paymentTransactionRepository.save(transaction).getId();
    }

    public void update(UUID id, UpdatePaymentTransactionRequest request) {
        PaymentTransaction transaction = paymentTransactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment transaction not found: " + id));
        transaction.update(request.eventType(), request.externalStatus(), request.payload());
    }

    public void delete(UUID id) {
        if (paymentTransactionRepository.findById(id).isEmpty()) {
            throw new EntityNotFoundException("Payment transaction not found: " + id);
        }
        paymentTransactionRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public PaymentTransactionResponse getOne(UUID id) {
        return paymentTransactionRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Payment transaction not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<PaymentTransactionResponse> getAll() {
        return paymentTransactionRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<PaymentTransactionResponse> getPage(int page, int size, String sortBy, String sortDir, String query) {
        var result = paymentTransactionRepository.findPage(page, size, sortBy, sortDir, query);
        List<PaymentTransactionResponse> content = result.content().stream().map(this::toResponse).toList();
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);
        return PageResponse.of(content, safePage, safeSize, result.totalElements(), sortBy, sortDir, query);
    }

    private PaymentTransactionResponse toResponse(PaymentTransaction transaction) {
        return new PaymentTransactionResponse(
                transaction.getId(),
                transaction.getPayment().getId(),
                transaction.getEventType(),
                transaction.getExternalStatus(),
                transaction.getPayload(),
                transaction.getCreatedAt()
        );
    }
}
