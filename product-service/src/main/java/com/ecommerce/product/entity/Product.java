package com.ecommerce.product.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    private String id;

    private String name;

    private String description;

    private BigDecimal price;

    private String category;

    private Integer stock;

    // MongoDB'nin gücü burada — her ürün farklı attribute'lara sahip olabilir
    // Telefon: ram, storage, color
    // Tişört: size, color, material
    private Map<String, Object> attributes;

    private boolean active = true;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}