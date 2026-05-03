package com.ecommerce.category.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {
    private String id;
    private String name;
    private String description;
    private String slug;
    private String parentId;
    private boolean active;
    private LocalDateTime createdAt;
    private List<CategoryResponse> subCategories;
}
