package com.ecommerce.inventory.service;

import com.ecommerce.inventory.dto.request.InventoryRequest;
import com.ecommerce.inventory.dto.response.InventoryResponse;
import com.ecommerce.inventory.entity.Inventory;
import com.ecommerce.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryResponse addStock(InventoryRequest request) {
        Inventory inventory = inventoryRepository.findByProductId(request.getProductId())
                .orElse(Inventory.builder()
                        .productId(request.getProductId())
                        .productName(request.getProductName())
                        .totalStock(0)
                        .reservedStock(0)
                        .availableStock(0)
                        .build());

        inventory.setTotalStock(inventory.getTotalStock() + request.getQuantity());
        inventory.setAvailableStock(inventory.getTotalStock() - inventory.getReservedStock());
        inventory.setUpdatedAt(LocalDateTime.now());

        inventory = inventoryRepository.save(inventory);
        log.info("Stock added for product: {} quantity: {}", request.getProductId(), request.getQuantity());
        return toResponse(inventory);
    }

    public void decreaseStock(String productId, Integer quantity) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Inventory not found: " + productId));

        if (inventory.getAvailableStock() < quantity) {
            throw new RuntimeException("Insufficient stock for product: " + productId);
        }

        inventory.setReservedStock(inventory.getReservedStock() + quantity);
        inventory.setAvailableStock(inventory.getAvailableStock() - quantity);
        inventory.setUpdatedAt(LocalDateTime.now());

        inventoryRepository.save(inventory);
        log.info("Stock decreased for product: {} quantity: {}", productId, quantity);
    }

    public InventoryResponse getInventory(String productId) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Inventory not found: " + productId));
        return toResponse(inventory);
    }

    private InventoryResponse toResponse(Inventory inventory) {
        return InventoryResponse.builder()
                .productId(inventory.getProductId())
                .productName(inventory.getProductName())
                .totalStock(inventory.getTotalStock())
                .reservedStock(inventory.getReservedStock())
                .availableStock(inventory.getAvailableStock())
                .build();
    }
}
