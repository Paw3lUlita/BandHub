package com.bandhub.zsi.ticketing;

import com.bandhub.zsi.ticketing.dto.AttendeeRowResponse;
import com.bandhub.zsi.ticketing.dto.ConcertTicketingSummaryResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/concerts/{concertId}/ticketing")
@PreAuthorize("hasRole('ADMIN')")
class ConcertTicketingAdminController {

    private final TicketingReportingService reportingService;

    ConcertTicketingAdminController(TicketingReportingService reportingService) {
        this.reportingService = reportingService;
    }

    @GetMapping("/summary")
    ResponseEntity<ConcertTicketingSummaryResponse> summary(@PathVariable UUID concertId) {
        return ResponseEntity.ok(reportingService.concertSummary(concertId));
    }

    @GetMapping("/attendees")
    ResponseEntity<List<AttendeeRowResponse>> attendees(@PathVariable UUID concertId) {
        return ResponseEntity.ok(reportingService.listAttendees(concertId));
    }

    @GetMapping(value = "/attendees/export", produces = "text/csv")
    ResponseEntity<byte[]> exportAttendees(@PathVariable UUID concertId) {
        List<AttendeeRowResponse> rows = reportingService.listAttendees(concertId);
        StringBuilder sb = new StringBuilder();
        sb.append("ticketCode,userId,orderId,poolName,purchaseDate\n");
        for (AttendeeRowResponse r : rows) {
            sb.append(escapeCsv(r.ticketCode())).append(',');
            sb.append(escapeCsv(r.userId())).append(',');
            sb.append(r.orderId() != null ? r.orderId() : "").append(',');
            sb.append(escapeCsv(r.poolName())).append(',');
            sb.append(r.purchaseDate() != null ? r.purchaseDate() : "").append('\n');
        }
        byte[] bytes = sb.toString().getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"attendees-" + concertId + ".csv\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(bytes);
    }

    private static String escapeCsv(String v) {
        if (v == null) {
            return "";
        }
        if (v.contains(",") || v.contains("\"") || v.contains("\n")) {
            return "\"" + v.replace("\"", "\"\"") + "\"";
        }
        return v;
    }
}
