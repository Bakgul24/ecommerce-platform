package com.ecommerce.category.dto.request;

import lombok.Data;

@Data
public class CategoryRequest {
    private String name;
    private String description;
    private String slug;
    private String parentId;
}