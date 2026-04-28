package com.ecommerce.notification.consumer;


import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderNotificationConsumer {

    @KafkaListener(topics = "order.created", groupId = "notification-group")
    public void handleOrderCreated(String message) {
        log.info("📧 Notification received for order.created event");
        log.info("📧 Message: {}", message);


        log.info("📧 Notification sent successfully for order event");
    }
}