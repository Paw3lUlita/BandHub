package com.bandhub.zsi.ecommerce;

import com.bandhub.zsi.ecommerce.domain.*;
import com.bandhub.zsi.ecommerce.dto.PlaceOrderCommand;
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

    public OrderPublicService(ProductRepository productRepository, OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional // KLUCZOWE: Cała metoda to jedna transakcja w bazie.
    public UUID placeOrder(PlaceOrderCommand command, String userId) {
        List<OrderItem> orderItems = new ArrayList<>();

        // 1. Przetwarzamy każdy produkt z koszyka
        command.items().forEach((productId, quantity) -> {

            // A. Pobieramy produkt (blokujemy go w kontekście transakcji)
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new EntityNotFoundException("Product not found: " + productId));

            // B. Zdejmujemy ze stanu (rzuca wyjątek, jeśli quantity > stock)
            // Dzięki @Transactional, ten stan zapisze się w bazie automatycznie na końcu metody!
            product.reduceStock(quantity);

            // C. Tworzymy pozycję zamówienia (Snapshot ceny z momentu zakupu)
            OrderItem item = new OrderItem(
                    product.getId(),
                    product.getName(),
                    product.getPrice(), // Kopiujemy obecną cenę
                    quantity
            );
            orderItems.add(item);
        });

        // 2. Tworzymy zamówienie (Agregat przelicza sumę całkowitą)
        Order order = Order.create(userId, orderItems);

        // 3. Zapisujemy zamówienie
        orderRepository.save(order);

        return order.getId();
    }
}
