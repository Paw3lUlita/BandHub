package com.bandhub.zsi.ecommerce;

import com.bandhub.zsi.ecommerce.domain.*;
import com.bandhub.zsi.ecommerce.dto.PlaceOrderCommand;
import com.bandhub.zsi.shared.Money;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderPublicService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final ShipmentRepository shipmentRepository;

    public OrderPublicService(
            ProductRepository productRepository,
            OrderRepository orderRepository,
            PaymentRepository paymentRepository,
            ShipmentRepository shipmentRepository
    ) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.shipmentRepository = shipmentRepository;
    }

    @Transactional
    public UUID placeOrder(PlaceOrderCommand command, String userId) {
        List<OrderItem> orderItems = new ArrayList<>();

        command.items().forEach((productId, quantity) -> {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new EntityNotFoundException("Product not found: " + productId));

            product.reduceStock(quantity);

            OrderItem item = new OrderItem(
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    quantity
            );
            orderItems.add(item);
        });

        Order order = Order.create(userId, orderItems);
        orderRepository.save(order);

        Money total = order.getTotalAmount();
        String provider = command.paymentProvider() != null && !command.paymentProvider().isBlank()
                ? command.paymentProvider() : "pending";

        Payment payment = Payment.create(
                UUID.randomUUID(),
                order,
                provider,
                null,
                "PENDING",
                total,
                null
        );
        paymentRepository.save(payment);

        Shipment shipment = Shipment.create(
                UUID.randomUUID(),
                order,
                null,
                null,
                "PENDING",
                null,
                null,
                command.deliveryAddress()
        );
        shipmentRepository.save(shipment);

        return order.getId();
    }
}
