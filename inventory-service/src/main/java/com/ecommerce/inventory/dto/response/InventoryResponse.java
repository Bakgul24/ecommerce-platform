package com.ecommerce.inventory.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryResponse {
    private String productId;
    private String productName;
    private Integer totalStock;
    private Integer reservedStock;
    private Integer availableStock;
}