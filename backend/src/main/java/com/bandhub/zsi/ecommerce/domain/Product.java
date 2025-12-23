package com.bandhub.zsi.ecommerce.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    public Product(String name, String description, Money price, int stockQuantity, ProductCategory category) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.category = category;
    }

    public static Product create(String name, String description, Money price, int stock, ProductCategory category) {
        return new Product(name, description, price, stock, category);
    }

    public void updateStock(int quantityChange) {
        int newStock = this.stockQuantity + quantityChange;
        if (newStock < 0) {
            throw new IllegalStateException("Stock cannot be negative");
        }
        this.stockQuantity = newStock;
    }
}