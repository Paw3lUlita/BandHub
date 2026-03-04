package com.bandhub.zsi.ticketing;

import com.bandhub.zsi.shared.Money;
import com.bandhub.zsi.shared.api.PageResponse;
import com.bandhub.zsi.ticketing.domain.Concert;
import com.bandhub.zsi.ticketing.domain.TicketOrder;
import com.bandhub.zsi.ticketing.dto.CreateTicketOrderRequest;
import com.bandhub.zsi.ticketing.dto.TicketOrderResponse;
import com.bandhub.zsi.ticketing.dto.UpdateTicketOrderRequest;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class TicketOrderAdminService {

    private final TicketOrderRepository ticketOrderRepository;
    private final ConcertRepository concertRepository;

    public TicketOrderAdminService(TicketOrderRepository ticketOrderRepository, ConcertRepository concertRepository) {
        this.ticketOrderRepository = ticketOrderRepository;
        this.concertRepository = concertRepository;
    }

    public UUID create(CreateTicketOrderRequest request) {
        Concert concert = concertRepository.findById(request.concertId())
                .orElseThrow(() -> new EntityNotFoundException("Concert not found: " + request.concertId()));
        TicketOrder order = TicketOrder.create(
                request.id(),
                request.userId(),
                concert,
                request.status(),
                new Money(request.totalAmount(), request.currency())
        );
        return ticketOrderRepository.save(order).getId();
    }

    public void update(UUID id, UpdateTicketOrderRequest request) {
        TicketOrder order = ticketOrderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ticket order not found: " + id));
        Concert concert = concertRepository.findById(request.concertId())
                .orElseThrow(() -> new EntityNotFoundException("Concert not found: " + request.concertId()));
        order.update(request.userId(), concert, request.status(), new Money(request.totalAmount(), request.currency()));
    }

    public void delete(UUID id) {
        if (ticketOrderRepository.findById(id).isEmpty()) {
            throw new EntityNotFoundException("Ticket order not found: " + id);
        }
        ticketOrderRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public TicketOrderResponse getOne(UUID id) {
        return ticketOrderRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Ticket order not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<TicketOrderResponse> getAll() {
        return ticketOrderRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<TicketOrderResponse> getPage(int page, int size, String sortBy, String sortDir, String query) {
        String normalizedQuery = query == null ? "" : query.trim().toLowerCase();
        boolean descending = "desc".equalsIgnoreCase(sortDir);
        List<TicketOrderResponse> filtered = ticketOrderRepository.findAll().stream()
                .map(this::toResponse)
                .filter(item -> normalizedQuery.isBlank()
                        || item.userId().toLowerCase().contains(normalizedQuery)
                        || item.status().toLowerCase().contains(normalizedQuery)
                        || item.concertId().toString().toLowerCase().contains(normalizedQuery))
                .sorted(resolveComparator(sortBy, descending))
                .toList();

        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);
        int fromIndex = safePage * safeSize;
        int toIndex = Math.min(fromIndex + safeSize, filtered.size());
        List<TicketOrderResponse> content = fromIndex >= filtered.size() ? List.of() : filtered.subList(fromIndex, toIndex);
        return PageResponse.of(content, safePage, safeSize, filtered.size(), sortBy, sortDir, query);
    }

    private Comparator<TicketOrderResponse> resolveComparator(String sortBy, boolean descending) {
        Comparator<TicketOrderResponse> comparator = switch (sortBy) {
            case "status" -> Comparator.comparing(TicketOrderResponse::status, String.CASE_INSENSITIVE_ORDER);
            case "totalAmount" -> Comparator.comparing(TicketOrderResponse::totalAmount);
            case "createdAt" -> Comparator.comparing(TicketOrderResponse::createdAt, Comparator.nullsLast(Comparator.naturalOrder()));
            default -> Comparator.comparing(TicketOrderResponse::createdAt, Comparator.nullsLast(Comparator.naturalOrder()));
        };
        return descending ? comparator.reversed() : comparator;
    }

    private TicketOrderResponse toResponse(TicketOrder order) {
        return new TicketOrderResponse(
                order.getId(),
                order.getUserId(),
                order.getConcert().getId(),
                order.getStatus(),
                order.getTotalAmount().amount(),
                order.getTotalAmount().currency(),
                order.getCreatedAt()
        );
    }
}
