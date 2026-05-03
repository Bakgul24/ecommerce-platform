package com.ecommerce.product.dto.request;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class ProductRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private String categoryId;
    private Integer stock;
    private Map<String, Object> attributes;
}