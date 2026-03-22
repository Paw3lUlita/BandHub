package com.bandhub.zsi.ticketing;

import com.bandhub.zsi.ticketing.dto.TicketingEventSnapshotResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/reports/ticketing")
@PreAuthorize("hasRole('ADMIN')")
class TicketingEventReportController {

    private final TicketingReportingService reportingService;

    TicketingEventReportController(TicketingReportingService reportingService) {
        this.reportingService = reportingService;
    }

    @GetMapping("/event-summary")
    ResponseEntity<TicketingEventSnapshotResponse> eventSummary(@RequestParam UUID concertId) {
        return ResponseEntity.ok(reportingService.eventSnapshot(concertId));
    }
}
