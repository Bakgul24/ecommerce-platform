package com.ecommerce.category.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "categories")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    private String id;

    private String name;

    private String description;

    private String slug;

    private String parentId;

    private boolean active = true;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}