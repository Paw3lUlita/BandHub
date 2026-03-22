package com.bandhub.zsi.ticketing;

import com.bandhub.zsi.ticketing.dto.PlaceTicketOrderCommand;
import com.bandhub.zsi.ticketing.dto.TicketPurchaseResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/public/ticket-orders")
class TicketOrderPublicController {

    private final TicketOrderPublicService service;

    TicketOrderPublicController(TicketOrderPublicService service) {
        this.service = service;
    }

    @PostMapping
    ResponseEntity<TicketPurchaseResponse> purchase(
            @RequestBody @Valid PlaceTicketOrderCommand command,
            Authentication authentication
    ) {
        String userId = authentication != null ? authentication.getName() : "anonymousUser";
        TicketPurchaseResponse body = service.purchase(command, userId);
        return ResponseEntity
                .created(URI.create("/api/public/ticket-orders/" + body.orderId()))
                .body(body);
    }
}
