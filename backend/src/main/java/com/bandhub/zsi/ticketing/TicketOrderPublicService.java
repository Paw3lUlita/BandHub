package com.bandhub.zsi.ticketing;

import com.bandhub.zsi.shared.Money;
import com.bandhub.zsi.ticketing.domain.*;
import com.bandhub.zsi.ticketing.dto.PlaceTicketOrderCommand;
import com.bandhub.zsi.ticketing.dto.TicketPurchaseResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class TicketOrderPublicService {

    private static final String ORDER_STATUS_PAID = "PAID";

    private final ConcertRepository concertRepository;
    private final TicketOrderRepository ticketOrderRepository;
    private final TicketOrderItemRepository ticketOrderItemRepository;
    private final TicketRepository ticketRepository;
    private final TicketCodeRepository ticketCodeRepository;

    public TicketOrderPublicService(
            ConcertRepository concertRepository,
            TicketOrderRepository ticketOrderRepository,
            TicketOrderItemRepository ticketOrderItemRepository,
            TicketRepository ticketRepository,
            TicketCodeRepository ticketCodeRepository
    ) {
        this.concertRepository = concertRepository;
        this.ticketOrderRepository = ticketOrderRepository;
        this.ticketOrderItemRepository = ticketOrderItemRepository;
        this.ticketRepository = ticketRepository;
        this.ticketCodeRepository = ticketCodeRepository;
    }

    @Transactional
    public TicketPurchaseResponse purchase(PlaceTicketOrderCommand command, String userId) {
        Concert concert = concertRepository.findById(command.concertId())
                .orElseThrow(() -> new EntityNotFoundException("Concert not found: " + command.concertId()));

        String currency = null;
        BigDecimal total = BigDecimal.ZERO;
        List<TicketPool> reservedPools = new ArrayList<>();
        List<Integer> reservedQty = new ArrayList<>();

        for (var entry : command.items().entrySet()) {
            UUID poolId = entry.getKey();
            int qty = entry.getValue();
            TicketPool pool = concert.getTicketPools().stream()
                    .filter(p -> p.getId().equals(poolId))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Pool does not belong to concert: " + poolId));
            pool.reserve(qty);
            if (currency == null) {
                currency = pool.getPrice().currency();
            } else if (!currency.equals(pool.getPrice().currency())) {
                throw new IllegalStateException("Mixed currencies in one order are not supported");
            }
            total = total.add(pool.getPrice().amount().multiply(BigDecimal.valueOf(qty)));
            reservedPools.add(pool);
            reservedQty.add(qty);
        }

        Money totalMoney = new Money(total, currency != null ? currency : "PLN");
        UUID orderId = UUID.randomUUID();
        TicketOrder order = TicketOrder.create(orderId, userId, concert, ORDER_STATUS_PAID, totalMoney);
        ticketOrderRepository.save(order);

        List<String> issuedCodes = new ArrayList<>();
        for (int i = 0; i < reservedPools.size(); i++) {
            TicketPool pool = reservedPools.get(i);
            int qty = reservedQty.get(i);
            TicketOrderItem item = TicketOrderItem.create(order, pool, qty, pool.getPrice());
            ticketOrderItemRepository.save(item);
            for (int n = 0; n < qty; n++) {
                String code = generateUniqueCode();
                UUID ticketId = UUID.randomUUID();
                Ticket ticket = Ticket.issue(ticketId, code, pool, userId, order);
                ticketRepository.save(ticket);
                TicketCode ticketCode = TicketCode.create(ticketId, code, "QR", "ACTIVE");
                ticketCodeRepository.save(ticketCode);
                issuedCodes.add(code);
            }
        }

        return new TicketPurchaseResponse(orderId, issuedCodes);
    }

    private String generateUniqueCode() {
        for (int attempt = 0; attempt < 8; attempt++) {
            String candidate = "BH-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
            if (!ticketCodeRepository.existsByCodeValue(candidate)) {
                return candidate;
            }
        }
        throw new IllegalStateException("Could not generate unique ticket code");
    }
}
