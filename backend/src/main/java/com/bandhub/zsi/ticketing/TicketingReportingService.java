package com.bandhub.zsi.ticketing;

import com.bandhub.zsi.ticketing.dto.AttendeeRowResponse;
import com.bandhub.zsi.ticketing.dto.ConcertTicketingSummaryResponse;
import com.bandhub.zsi.ticketing.dto.TicketPoolSalesResponse;
import com.bandhub.zsi.ticketing.dto.TicketingEventSnapshotResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class TicketingReportingService {

    private final JdbcTemplate jdbc;
    private final ConcertRepository concertRepository;

    public TicketingReportingService(JdbcTemplate jdbc, ConcertRepository concertRepository) {
        this.jdbc = jdbc;
        this.concertRepository = concertRepository;
    }

    public ConcertTicketingSummaryResponse concertSummary(UUID concertId) {
        var concert = concertRepository.findById(concertId)
                .orElseThrow(() -> new EntityNotFoundException("Concert not found: " + concertId));
        int cap = concert.getVenue() != null ? concert.getVenue().getCapacity() : 0;

        String sql = """
                SELECT tp.id, tp.name, tp.remaining_quantity, tp.total_quantity, tp.price, tp.currency,
                       COALESCE(s.sold, 0) AS sold,
                       COALESCE(s.revenue, 0) AS revenue
                FROM ticket_pools tp
                LEFT JOIN (
                    SELECT ti.ticket_pool_id,
                           SUM(ti.quantity) AS sold,
                           SUM(ti.quantity * ti.unit_price) AS revenue
                    FROM ticket_order_items ti
                    INNER JOIN ticket_orders o ON o.id = ti.ticket_order_id
                    WHERE o.concert_id = ? AND COALESCE(UPPER(TRIM(o.status)), '') <> 'CANCELLED'
                    GROUP BY ti.ticket_pool_id
                ) s ON s.ticket_pool_id = tp.id
                WHERE tp.concert_id = ?
                ORDER BY tp.name
                """;

        List<TicketPoolSalesResponse> pools = jdbc.query(sql, (rs, rn) -> {
            BigDecimal rev = rs.getBigDecimal("revenue");
            if (rev == null) {
                rev = BigDecimal.ZERO;
            }
            String cur = rs.getString("currency");
            return new TicketPoolSalesResponse(
                    rs.getObject("id", UUID.class),
                    rs.getString("name"),
                    rs.getLong("sold"),
                    rs.getInt("remaining_quantity"),
                    rs.getInt("total_quantity"),
                    rev,
                    cur != null && !cur.isBlank() ? cur : "PLN"
            );
        }, concertId, concertId);

        long totalSold = pools.stream().mapToLong(TicketPoolSalesResponse::sold).sum();
        BigDecimal totalRev = pools.stream()
                .map(TicketPoolSalesResponse::revenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        String currency = pools.stream()
                .map(TicketPoolSalesResponse::currency)
                .filter(c -> c != null && !c.isBlank())
                .findFirst()
                .orElse("PLN");

        return new ConcertTicketingSummaryResponse(
                concertId,
                concert.getName(),
                cap,
                totalSold,
                totalRev,
                currency,
                pools
        );
    }

    public List<AttendeeRowResponse> listAttendees(UUID concertId) {
        if (concertRepository.findById(concertId).isEmpty()) {
            throw new EntityNotFoundException("Concert not found: " + concertId);
        }
        String sql = """
                SELECT t.ticket_code, t.user_id, t.ticket_order_id, tp.name AS pool_name, t.purchase_date
                FROM tickets t
                INNER JOIN ticket_pools tp ON tp.id = t.pool_id
                WHERE tp.concert_id = ?
                ORDER BY t.purchase_date DESC NULLS LAST, t.ticket_code
                """;
        return jdbc.query(sql, (rs, rn) -> new AttendeeRowResponse(
                rs.getString("ticket_code"),
                rs.getString("user_id"),
                rs.getObject("ticket_order_id", UUID.class),
                rs.getString("pool_name"),
                rs.getTimestamp("purchase_date") != null
                        ? rs.getTimestamp("purchase_date").toLocalDateTime()
                        : null
        ), concertId);
    }

    public TicketingEventSnapshotResponse eventSnapshot(UUID concertId) {
        ConcertTicketingSummaryResponse summary = concertSummary(concertId);
        long remaining = summary.pools().stream().mapToLong(TicketPoolSalesResponse::remaining).sum();
        double occ = summary.venueCapacity() > 0
                ? Math.min(100.0, (summary.totalSold() * 100.0) / summary.venueCapacity())
                : 0.0;
        return new TicketingEventSnapshotResponse(
                summary.concertId(),
                summary.concertName(),
                summary.totalSold(),
                remaining,
                summary.totalRevenue(),
                summary.currency(),
                summary.venueCapacity(),
                occ
        );
    }
}
