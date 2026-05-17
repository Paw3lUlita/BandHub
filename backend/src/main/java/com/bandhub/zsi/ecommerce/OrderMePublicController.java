package com.bandhub.zsi.ecommerce;

import com.bandhub.zsi.ecommerce.dto.OrderDetailsResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Historia zamowien merch zalogowanego fana (filtrowana po JWT subject).
 */
@RestController
@RequestMapping("/api/public/orders")
class OrderMePublicController {

    private final OrderAdminService orderAdminService;

    OrderMePublicController(OrderAdminService orderAdminService) {
        this.orderAdminService = orderAdminService;
    }

    @GetMapping("/me")
    ResponseEntity<List<OrderDetailsResponse>> getMyOrders(Authentication authentication) {
        String userId = authentication.getName();
        return ResponseEntity.ok(orderAdminService.getOrdersForUser(userId));
    }
}
