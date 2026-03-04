package com.bandhub.zsi.ticketing;

import com.bandhub.zsi.shared.Money;
import com.bandhub.zsi.shared.api.PageResponse;
import com.bandhub.zsi.ticketing.domain.TicketOrder;
import com.bandhub.zsi.ticketing.domain.TicketOrderItem;
import com.bandhub.zsi.ticketing.domain.TicketPool;
import com.bandhub.zsi.ticketing.dto.CreateTicketOrderItemRequest;
import com.bandhub.zsi.ticketing.dto.TicketOrderItemResponse;
import com.bandhub.zsi.ticketing.dto.UpdateTicketOrderItemRequest;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class TicketOrderItemAdminService {

    private final TicketOrderItemRepository ticketOrderItemRepository;
    private final TicketOrderRepository ticketOrderRepository;
    private final TicketPoolLookupRepository ticketPoolLookupRepository;

    public TicketOrderItemAdminService(TicketOrderItemRepository ticketOrderItemRepository, TicketOrderRepository ticketOrderRepository, TicketPoolLookupRepository ticketPoolLookupRepository) {
        this.ticketOrderItemRepository = ticketOrderItemRepository;
        this.ticketOrderRepository = ticketOrderRepository;
        this.ticketPoolLookupRepository = ticketPoolLookupRepository;
    }

    public UUID create(CreateTicketOrderItemRequest request) {
        TicketOrder order = ticketOrderRepository.findById(request.ticketOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Ticket order not found: " + request.ticketOrderId()));
        TicketPool pool = ticketPoolLookupRepository.findById(request.ticketPoolId())
                .orElseThrow(() -> new EntityNotFoundException("Ticket pool not found: " + request.ticketPoolId()));
        TicketOrderItem item = TicketOrderItem.create(order, pool, request.quantity(), new Money(request.unitPrice(), request.currency()));
        return ticketOrderItemRepository.save(item).getId();
    }

    public void update(UUID id, UpdateTicketOrderItemRequest request) {
        TicketOrderItem item = ticketOrderItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ticket order item not found: " + id));
        TicketOrder order = ticketOrderRepository.findById(request.ticketOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Ticket order not found: " + request.ticketOrderId()));
        TicketPool pool = ticketPoolLookupRepository.findById(request.ticketPoolId())
                .orElseThrow(() -> new EntityNotFoundException("Ticket pool not found: " + request.ticketPoolId()));
        item.update(order, pool, request.quantity(), new Money(request.unitPrice(), request.currency()));
    }

    public void delete(UUID id) {
        if (ticketOrderItemRepository.findById(id).isEmpty()) {
            throw new EntityNotFoundException("Ticket order item not found: " + id);
        }
        ticketOrderItemRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public TicketOrderItemResponse getOne(UUID id) {
        return ticketOrderItemRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Ticket order item not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<TicketOrderItemResponse> getAll() {
        return ticketOrderItemRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<TicketOrderItemResponse> getPage(int page, int size, String sortBy, String sortDir, String query) {
        String normalizedQuery = query == null ? "" : query.trim().toLowerCase();
        boolean descending = "desc".equalsIgnoreCase(sortDir);
        List<TicketOrderItemResponse> filtered = ticketOrderItemRepository.findAll().stream()
                .map(this::toResponse)
                .filter(item -> normalizedQuery.isBlank()
                        || item.ticketOrderId().toString().toLowerCase().contains(normalizedQuery)
                        || item.ticketPoolId().toString().toLowerCase().contains(normalizedQuery))
                .sorted(resolveComparator(sortBy, descending))
                .toList();
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);
        int fromIndex = safePage * safeSize;
        int toIndex = Math.min(fromIndex + safeSize, filtered.size());
        List<TicketOrderItemResponse> content = fromIndex >= filtered.size() ? List.of() : filtered.subList(fromIndex, toIndex);
        return PageResponse.of(content, safePage, safeSize, filtered.size(), sortBy, sortDir, query);
    }

    private Comparator<TicketOrderItemResponse> resolveComparator(String sortBy, boolean descending) {
        Comparator<TicketOrderItemResponse> comparator = switch (sortBy) {
            case "quantity" -> Comparator.comparing(TicketOrderItemResponse::quantity);
            case "unitPrice" -> Comparator.comparing(TicketOrderItemResponse::unitPrice);
            default -> Comparator.comparing(TicketOrderItemResponse::id);
        };
        return descending ? comparator.reversed() : comparator;
    }

    private TicketOrderItemResponse toResponse(TicketOrderItem item) {
        return new TicketOrderItemResponse(
                item.getId(),
                item.getTicketOrder().getId(),
                item.getTicketPool().getId(),
                item.getQuantity(),
                item.getUnitPrice().amount(),
                item.getUnitPrice().currency()
        );
    }
}
