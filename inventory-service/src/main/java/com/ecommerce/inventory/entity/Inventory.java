package com.ecommerce.inventory.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "inventories")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {

    @Id
    private String id;

    private String productId;

    private String productName;

    private Integer totalStock;

    private Integer reservedStock;

    private Integer availableStock;

    // Optimistic locking için — race condition önler
    @Version
    private Long version;

    private LocalDateTime updatedAt;
}