package com.ecommerce.inventory.dto.request;

import lombok.Data;

@Data
public class InventoryRequest {
    private String productId;
    private String productName;
    private Integer quantity;
}
