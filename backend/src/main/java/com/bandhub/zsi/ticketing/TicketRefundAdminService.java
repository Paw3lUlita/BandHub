package com.bandhub.zsi.ticketing;

import com.bandhub.zsi.shared.Money;
import com.bandhub.zsi.shared.api.PageResponse;
import com.bandhub.zsi.ticketing.domain.TicketOrder;
import com.bandhub.zsi.ticketing.domain.TicketRefund;
import com.bandhub.zsi.ticketing.dto.CreateTicketRefundRequest;
import com.bandhub.zsi.ticketing.dto.TicketRefundResponse;
import com.bandhub.zsi.ticketing.dto.UpdateTicketRefundRequest;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class TicketRefundAdminService {

    private final TicketRefundRepository ticketRefundRepository;
    private final TicketOrderRepository ticketOrderRepository;

    public TicketRefundAdminService(TicketRefundRepository ticketRefundRepository, TicketOrderRepository ticketOrderRepository) {
        this.ticketRefundRepository = ticketRefundRepository;
        this.ticketOrderRepository = ticketOrderRepository;
    }

    public UUID create(CreateTicketRefundRequest request) {
        TicketOrder ticketOrder = request.ticketOrderId() == null
                ? null
                : ticketOrderRepository.findById(request.ticketOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Ticket order not found: " + request.ticketOrderId()));

        TicketRefund refund = TicketRefund.create(
                ticketOrder,
                request.ticketId(),
                new Money(request.amount(), request.currency()),
                request.reason(),
                request.status(),
                request.resolvedAt()
        );
        return ticketRefundRepository.save(refund).getId();
    }

    public void update(UUID id, UpdateTicketRefundRequest request) {
        TicketRefund refund = ticketRefundRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ticket refund not found: " + id));
        TicketOrder ticketOrder = request.ticketOrderId() == null
                ? null
                : ticketOrderRepository.findById(request.ticketOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Ticket order not found: " + request.ticketOrderId()));
        refund.update(
                ticketOrder,
                request.ticketId(),
                new Money(request.amount(), request.currency()),
                request.reason(),
                request.status(),
                request.resolvedAt()
        );
    }

    public void delete(UUID id) {
        if (ticketRefundRepository.findById(id).isEmpty()) {
            throw new EntityNotFoundException("Ticket refund not found: " + id);
        }
        ticketRefundRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public TicketRefundResponse getOne(UUID id) {
        return ticketRefundRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Ticket refund not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<TicketRefundResponse> getAll() {
        return ticketRefundRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<TicketRefundResponse> getPage(int page, int size, String sortBy, String sortDir, String query) {
        String normalizedQuery = query == null ? "" : query.trim().toLowerCase();
        boolean descending = "desc".equalsIgnoreCase(sortDir);
        List<TicketRefundResponse> filtered = ticketRefundRepository.findAll().stream()
                .map(this::toResponse)
                .filter(item -> normalizedQuery.isBlank()
                        || item.status().toLowerCase().contains(normalizedQuery)
                        || (item.reason() != null && item.reason().toLowerCase().contains(normalizedQuery))
                        || (item.ticketOrderId() != null && item.ticketOrderId().toString().toLowerCase().contains(normalizedQuery))
                        || (item.ticketId() != null && item.ticketId().toString().toLowerCase().contains(normalizedQuery)))
                .sorted(resolveComparator(sortBy, descending))
                .toList();
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);
        int fromIndex = safePage * safeSize;
        int toIndex = Math.min(fromIndex + safeSize, filtered.size());
        List<TicketRefundResponse> content = fromIndex >= filtered.size() ? List.of() : filtered.subList(fromIndex, toIndex);
        return PageResponse.of(content, safePage, safeSize, filtered.size(), sortBy, sortDir, query);
    }

    private Comparator<TicketRefundResponse> resolveComparator(String sortBy, boolean descending) {
        Comparator<TicketRefundResponse> comparator = switch (sortBy) {
            case "status" -> Comparator.comparing(TicketRefundResponse::status, String.CASE_INSENSITIVE_ORDER);
            case "requestedAt" -> Comparator.comparing(TicketRefundResponse::requestedAt, Comparator.nullsLast(Comparator.naturalOrder()));
            case "resolvedAt" -> Comparator.comparing(TicketRefundResponse::resolvedAt, Comparator.nullsLast(Comparator.naturalOrder()));
            default -> Comparator.comparing(TicketRefundResponse::requestedAt, Comparator.nullsLast(Comparator.naturalOrder()));
        };
        return descending ? comparator.reversed() : comparator;
    }

    private TicketRefundResponse toResponse(TicketRefund refund) {
        return new TicketRefundResponse(
                refund.getId(),
                refund.getTicketOrder() == null ? null : refund.getTicketOrder().getId(),
                refund.getTicketId(),
                refund.getAmount().amount(),
                refund.getAmount().currency(),
                refund.getReason(),
                refund.getStatus(),
                refund.getRequestedAt(),
                refund.getResolvedAt()
        );
    }
}
