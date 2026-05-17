package com.bandhub.zsi.ticketing;

import com.bandhub.zsi.shared.Money;
import com.bandhub.zsi.shared.api.PageResponse;
import com.bandhub.zsi.ticketing.domain.TicketOrder;
import com.bandhub.zsi.ticketing.domain.TicketRefund;
import com.bandhub.zsi.ticketing.dto.CreateTicketRefundRequest;
import com.bandhub.zsi.ticketing.dto.TicketRefundResponse;
import com.bandhub.zsi.ticketing.dto.UpdateTicketRefundRequest;
import com.bandhub.zsi.user.UserLookupService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class TicketRefundAdminService {

    private final TicketRefundRepository ticketRefundRepository;
    private final TicketOrderRepository ticketOrderRepository;
    private final TicketRepository ticketRepository;
    private final UserLookupService userLookupService;

    public TicketRefundAdminService(
            TicketRefundRepository ticketRefundRepository,
            TicketOrderRepository ticketOrderRepository,
            TicketRepository ticketRepository,
            UserLookupService userLookupService
    ) {
        this.ticketRefundRepository = ticketRefundRepository;
        this.ticketOrderRepository = ticketOrderRepository;
        this.ticketRepository = ticketRepository;
        this.userLookupService = userLookupService;
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
        var result = ticketRefundRepository.findPage(page, size, sortBy, sortDir, query);
        List<TicketRefundResponse> content = result.content().stream().map(this::toResponse).toList();
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);
        return PageResponse.of(content, safePage, safeSize, result.totalElements(), sortBy, sortDir, query);
    }

    private TicketRefundResponse toResponse(TicketRefund refund) {
        String username = null;
        String ticketCode = null;
        if (refund.getTicketOrder() != null) {
            username = userLookupService.usernameOrId(refund.getTicketOrder().getUserId());
        } else if (refund.getTicketId() != null) {
            username = ticketRepository.findById(refund.getTicketId())
                    .map(t -> userLookupService.usernameOrId(t.getUserId()))
                    .orElse(null);
        }
        if (refund.getTicketId() != null) {
            ticketCode = ticketRepository.findById(refund.getTicketId())
                    .map(t -> t.getTicketCode())
                    .orElse(null);
        }
        return new TicketRefundResponse(
                refund.getId(),
                refund.getTicketOrder() == null ? null : refund.getTicketOrder().getId(),
                refund.getTicketId(),
                ticketCode,
                username,
                refund.getAmount().amount(),
                refund.getAmount().currency(),
                refund.getReason(),
                refund.getStatus(),
                refund.getRequestedAt(),
                refund.getResolvedAt()
        );
    }
}
