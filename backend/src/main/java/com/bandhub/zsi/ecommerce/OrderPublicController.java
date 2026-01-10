package com.bandhub.zsi.ecommerce;

import com.bandhub.zsi.ecommerce.dto.PlaceOrderCommand;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/public/orders")
class OrderPublicController {

    private final OrderPublicService service;

    OrderPublicController(OrderPublicService service) {
        this.service = service;
    }

    @PostMapping
    ResponseEntity<Void> placeOrder(@RequestBody PlaceOrderCommand command, Authentication authentication) {
        // 1. Wyciągamy ID użytkownika z tokena (Subject w JWT)
        // Spring Security automatycznie wstrzykuje obiekt Authentication, jeśli user jest zalogowany.
        String userId = authentication.getName();

        // 2. Wołamy logikę biznesową
        UUID orderId = service.placeOrder(command, userId);

        // 3. Zwracamy 201 Created z nagłówkiem Location
        return ResponseEntity.created(URI.create("/api/public/orders/" + orderId)).build();
    }
}
