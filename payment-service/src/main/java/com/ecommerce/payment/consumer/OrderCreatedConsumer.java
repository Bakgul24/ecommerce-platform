package com.ecommerce.payment.consumer;

import com.ecommerce.payment.service.PaymentService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCreatedConsumer {

    private final PaymentService paymentService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "order.created", groupId = "payment-group")
    public void handleOrderCreated(String message) {
        try {
            log.info("Payment service received order.created event");

            String json = message;
            if (message.startsWith("\"")) {
                json = objectMapper.readValue(message, String.class);
            }

            JsonNode event = objectMapper.readTree(json);

            Long orderId = event.get("orderId").asLong();
            Long userId = event.get("userId").asLong();
            BigDecimal totalAmount = new BigDecimal(event.get("totalAmount").asText());

            paymentService.processPayment(orderId, userId, totalAmount);

        } catch (Exception e) {
            log.error("Error processing payment for order: {}", e.getMessage());
        }
    }
}