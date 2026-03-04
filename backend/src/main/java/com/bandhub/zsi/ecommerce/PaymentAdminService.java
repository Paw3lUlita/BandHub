package com.bandhub.zsi.ecommerce;

import com.bandhub.zsi.ecommerce.domain.Order;
import com.bandhub.zsi.ecommerce.domain.Payment;
import com.bandhub.zsi.ecommerce.dto.CreatePaymentRequest;
import com.bandhub.zsi.ecommerce.dto.PaymentResponse;
import com.bandhub.zsi.ecommerce.dto.UpdatePaymentRequest;
import com.bandhub.zsi.shared.Money;
import com.bandhub.zsi.shared.api.PageResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PaymentAdminService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public PaymentAdminService(PaymentRepository paymentRepository, OrderRepository orderRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    public UUID create(CreatePaymentRequest request) {
        Order order = orderRepository.findById(request.orderId())
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + request.orderId()));

        Payment payment = Payment.create(
                request.id(),
                order,
                request.provider(),
                request.providerPaymentId(),
                request.status(),
                new Money(request.amount(), request.currency()),
                request.paidAt()
        );

        return paymentRepository.save(payment).getId();
    }

    public void update(UUID id, UpdatePaymentRequest request) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found: " + id));

        payment.update(
                request.provider(),
                request.providerPaymentId(),
                request.status(),
                new Money(request.amount(), request.currency()),
                request.paidAt()
        );
    }

    public void delete(UUID id) {
        if (paymentRepository.findById(id).isEmpty()) {
            throw new EntityNotFoundException("Payment not found: " + id);
        }
        paymentRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getOne(UUID id) {
        return paymentRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getAll() {
        return paymentRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<PaymentResponse> getPage(int page, int size, String sortBy, String sortDir, String query) {
        String normalizedQuery = query == null ? "" : query.trim().toLowerCase();
        boolean descending = "desc".equalsIgnoreCase(sortDir);

        List<PaymentResponse> filtered = paymentRepository.findAll().stream()
                .map(this::toResponse)
                .filter(payment -> normalizedQuery.isBlank()
                        || payment.status().toLowerCase().contains(normalizedQuery)
                        || payment.orderId().toString().toLowerCase().contains(normalizedQuery)
                        || (payment.provider() != null && payment.provider().toLowerCase().contains(normalizedQuery)))
                .sorted(resolveComparator(sortBy, descending))
                .toList();

        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);
        int fromIndex = safePage * safeSize;
        int toIndex = Math.min(fromIndex + safeSize, filtered.size());
        List<PaymentResponse> content = fromIndex >= filtered.size()
                ? List.of()
                : filtered.subList(fromIndex, toIndex);

        return PageResponse.of(content, safePage, safeSize, filtered.size(), sortBy, sortDir, query);
    }

    private Comparator<PaymentResponse> resolveComparator(String sortBy, boolean descending) {
        Comparator<PaymentResponse> comparator = switch (sortBy) {
            case "status" -> Comparator.comparing(PaymentResponse::status, String.CASE_INSENSITIVE_ORDER);
            case "amount" -> Comparator.comparing(PaymentResponse::amount);
            case "paidAt" -> Comparator.comparing(PaymentResponse::paidAt, Comparator.nullsLast(Comparator.naturalOrder()));
            case "createdAt" -> Comparator.comparing(PaymentResponse::createdAt, Comparator.nullsLast(Comparator.naturalOrder()));
            default -> Comparator.comparing(PaymentResponse::createdAt, Comparator.nullsLast(Comparator.naturalOrder()));
        };
        return descending ? comparator.reversed() : comparator;
    }

    private PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getOrder().getId(),
                payment.getProvider(),
                payment.getProviderPaymentId(),
                payment.getStatus(),
                payment.getAmount().amount(),
                payment.getAmount().currency(),
                payment.getPaidAt(),
                payment.getCreatedAt()
        );
    }
}
