package com.bandhub.zsi.ecommerce.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import java.util.UUID;

@Entity
@Table(name = "product_categories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductCategory {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;

    private ProductCategory(String name) {
        validateAndSetName(name);
    }

    public static ProductCategory create(String name) {
        return new ProductCategory(name);
    }

    public void changeName(String name) {
        validateAndSetName(name);
    }

    private void validateAndSetName(String name) {
        Assert.hasText(name, "Category name cannot be empty");
        this.name = name;
    }
}