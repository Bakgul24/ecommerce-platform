package com.ecommerce.order.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.util.Map;

@FeignClient(name = "product-service", url = "http://localhost:8082")
public interface ProductClient {

    @GetMapping("/api/products/{id}")
    ProductResponse getProductById(@PathVariable String id);

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class ProductResponse {
        private String id;
        private String name;
        private BigDecimal price;
        private Integer stock;
        private Map<String, Object> attributes;
    }
}
