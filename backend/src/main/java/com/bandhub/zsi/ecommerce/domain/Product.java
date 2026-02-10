package com.bandhub.zsi.ecommerce.domain;

import com.bandhub.zsi.shared.Money;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert; // Do walidacji

import java.util.UUID;

@Entity
@Table(name = "products")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;

    @Column(length = 2000)
    private String description;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "price")),
            @AttributeOverride(name = "currency", column = @Column(name = "currency"))
    })
    private Money price;

    @Column(name = "stock_quantity")
    private int stockQuantity;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private ProductCategory category;

    public static Product create(String name, String description, Money price, int stock, ProductCategory category) {
        return new Product(name, description, price, stock, category);
    }

    private Product(String name, String description, Money price, int stockQuantity, ProductCategory category) {
        changeDetails(name, description, price, stockQuantity, category);
    }

    public void changeDetails(String name, String description, Money price, int stockQuantity, ProductCategory category) {
        Assert.hasText(name, "Product name cannot be empty");
        Assert.notNull(price, "Price is required");
        Assert.notNull(category, "Category is required");

        if (stockQuantity < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }

        // 2. Zmiana stanu
        this.name = name;
        this.description = description;
        this.price = price; // Podmieniamy cały obiekt Money (Value Object)
        this.stockQuantity = stockQuantity;
        this.category = category;
    }

    public void reduceStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity to reduce must be positive");
        }
        if (this.stockQuantity < quantity) {
            throw new IllegalStateException("Not enough stock for product: " + this.id);
        }
        this.stockQuantity = this.stockQuantity - quantity;
    }
}