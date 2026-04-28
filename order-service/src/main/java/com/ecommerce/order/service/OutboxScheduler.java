package com.ecommerce.order.service;

import com.ecommerce.order.entity.OutboxEvent;
import com.ecommerce.order.entity.OutboxStatus;
import com.ecommerce.order.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@EnableScheduling
public class OutboxScheduler {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Scheduled(fixedDelay = 5000) // Her 5 saniyede bir çalışır
    @Transactional
    public void processOutboxEvents() {
        List<OutboxEvent> pendingEvents = outboxEventRepository
                .findByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING);

        if (pendingEvents.isEmpty()) return;

        log.info("Processing {} outbox events", pendingEvents.size());

        for (OutboxEvent event : pendingEvents) {
            try {
                // Kafka topic'i event tipinden belirle
                String topic = resolveTopicName(event.getEventType());

                // Kafka'ya gönder
                kafkaTemplate.send(topic, event.getAggregateId(), event.getPayload());

                // Başarılı — SENT olarak işaretle
                event.setStatus(OutboxStatus.SENT);
                event.setSentAt(LocalDateTime.now());
                log.info("Outbox event sent to Kafka: {} - {}", event.getEventType(), event.getAggregateId());

            } catch (Exception e) {
                // Başarısız — retry sayısını artır
                event.setRetryCount(event.getRetryCount() + 1);

                if (event.getRetryCount() >= 3) {
                    event.setStatus(OutboxStatus.FAILED);
                    log.error("Outbox event failed after 3 retries: {}", event.getId());
                } else {
                    log.warn("Outbox event retry {}: {}", event.getRetryCount(), event.getId());
                }
            }

            outboxEventRepository.save(event);
        }
    }

    private String resolveTopicName(String eventType) {
        return switch (eventType) {
            case "ORDER_CREATED" -> "order.created";
            case "ORDER_CANCELLED" -> "order.cancelled";
            default -> "order.events";
        };
    }
}
