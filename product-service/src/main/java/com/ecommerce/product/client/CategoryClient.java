package com.ecommerce.product.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "category-service")
public interface CategoryClient {

    @GetMapping("/api/categories/{id}")
    CategoryResponse getCategoryById(@PathVariable String id);

    @GetMapping("/api/categories/slug/{slug}")
    CategoryResponse getCategoryBySlug(@PathVariable String slug);

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class CategoryResponse {
        private String id;
        private String name;
        private String description;
        private String slug;
    }
}