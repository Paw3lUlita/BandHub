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
        var result = paymentRepository.findPage(page, size, sortBy, sortDir, query);
        List<PaymentResponse> content = result.content().stream().map(this::toResponse).toList();
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);
        return PageResponse.of(content, safePage, safeSize, result.totalElements(), sortBy, sortDir, query);
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
