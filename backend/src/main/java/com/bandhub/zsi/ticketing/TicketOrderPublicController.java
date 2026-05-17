package com.bandhub.zsi.ticketing;

import com.bandhub.zsi.ticketing.dto.MyTicketOrderResponse;
import com.bandhub.zsi.ticketing.dto.PlaceTicketOrderCommand;
import com.bandhub.zsi.ticketing.dto.TicketPurchaseResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/public/ticket-orders")
class TicketOrderPublicController {

    private final TicketOrderPublicService service;

    TicketOrderPublicController(TicketOrderPublicService service) {
        this.service = service;
    }

    @GetMapping("/me")
    ResponseEntity<List<MyTicketOrderResponse>> getMyOrders(Authentication authentication) {
        String userId = authentication.getName();
        return ResponseEntity.ok(service.getOrdersForUser(userId));
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
