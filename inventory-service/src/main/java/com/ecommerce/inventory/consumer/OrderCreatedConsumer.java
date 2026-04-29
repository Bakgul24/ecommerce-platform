package com.ecommerce.inventory.consumer;

import com.ecommerce.inventory.service.InventoryService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCreatedConsumer {

    private final InventoryService inventoryService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "order.created", groupId = "inventory-group")
    public void handleOrderCreated(String message) {
        try {
            log.info("Inventory service received order.created event: {}", message);

            String json = message;
            if (message.startsWith("\"")) {
                json = objectMapper.readValue(message, String.class);
            }

            JsonNode event = objectMapper.readTree(json);
            JsonNode items = event.get("items");

            for (JsonNode item : items) {
                String productId = item.get("productId").asText();
                Integer quantity = item.get("quantity").asInt();

                try {
                    inventoryService.decreaseStock(productId, quantity);
                    log.info("Stock decreased for product: {} quantity: {}", productId, quantity);
                } catch (RuntimeException e) {
                    log.error("Failed to decrease stock for product: {} - {}", productId, e.getMessage());
                }
            }

        } catch (Exception e) {
            log.error("Error processing order.created event: {}", e.getMessage());
        }
    }
}