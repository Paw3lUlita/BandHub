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

import java.util.Comparator;
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
        String normalizedQuery = query == null ? "" : query.trim().toLowerCase();
        boolean descending = "desc".equalsIgnoreCase(sortDir);

        List<PaymentTransactionResponse> filtered = paymentTransactionRepository.findAll().stream()
                .map(this::toResponse)
                .filter(item -> normalizedQuery.isBlank()
                        || item.eventType().toLowerCase().contains(normalizedQuery)
                        || (item.externalStatus() != null && item.externalStatus().toLowerCase().contains(normalizedQuery))
                        || item.paymentId().toString().toLowerCase().contains(normalizedQuery))
                .sorted(resolveComparator(sortBy, descending))
                .toList();

        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);
        int fromIndex = safePage * safeSize;
        int toIndex = Math.min(fromIndex + safeSize, filtered.size());
        List<PaymentTransactionResponse> content = fromIndex >= filtered.size()
                ? List.of()
                : filtered.subList(fromIndex, toIndex);

        return PageResponse.of(content, safePage, safeSize, filtered.size(), sortBy, sortDir, query);
    }

    private Comparator<PaymentTransactionResponse> resolveComparator(String sortBy, boolean descending) {
        Comparator<PaymentTransactionResponse> comparator = switch (sortBy) {
            case "eventType" -> Comparator.comparing(PaymentTransactionResponse::eventType, String.CASE_INSENSITIVE_ORDER);
            case "createdAt" -> Comparator.comparing(PaymentTransactionResponse::createdAt, Comparator.nullsLast(Comparator.naturalOrder()));
            default -> Comparator.comparing(PaymentTransactionResponse::createdAt, Comparator.nullsLast(Comparator.naturalOrder()));
        };
        return descending ? comparator.reversed() : comparator;
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
