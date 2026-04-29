package com.ecommerce.payment.service;

import com.ecommerce.payment.entity.Payment;
import com.ecommerce.payment.entity.PaymentStatus;
import com.ecommerce.payment.event.PaymentCompletedEvent;
import com.ecommerce.payment.event.PaymentFailedEvent;
import com.ecommerce.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public void processPayment(Long orderId, Long userId, BigDecimal amount) {
        // Idempotency key — orderId bazlı, aynı sipariş için iki kez ödeme yapılmaz
        String idempotencyKey = "order-" + orderId;

        // Daha önce işlenmiş mi kontrol et
        Optional<Payment> existing = paymentRepository.findByIdempotencyKey(idempotencyKey);
        if (existing.isPresent()) {
            log.warn("Duplicate payment request for orderId: {} - skipping", orderId);
            return;
        }

        // Ödemeyi kaydet
        Payment payment = Payment.builder()
                .orderId(orderId)
                .userId(userId)
                .amount(amount)
                .status(PaymentStatus.PENDING)
                .idempotencyKey(idempotencyKey)
                .build();

        payment = paymentRepository.save(payment);

        // Ödemeyi simüle et — %80 başarılı, %20 başarısız
        boolean success = new Random().nextInt(10) < 8;

        if (success) {
            payment.setStatus(PaymentStatus.COMPLETED);
            paymentRepository.save(payment);

            PaymentCompletedEvent event = PaymentCompletedEvent.builder()
                    .paymentId(payment.getId())
                    .orderId(orderId)
                    .userId(userId)
                    .amount(amount)
                    .build();

            kafkaTemplate.send("payment.completed", orderId.toString(), event);
            log.info("Payment completed for orderId: {}", orderId);

        } else {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailureReason("Insufficient funds");
            paymentRepository.save(payment);

            PaymentFailedEvent event = PaymentFailedEvent.builder()
                    .paymentId(payment.getId())
                    .orderId(orderId)
                    .userId(userId)
                    .amount(amount)
                    .reason("Insufficient funds")
                    .build();

            kafkaTemplate.send("payment.failed", orderId.toString(), event);
            log.warn("Payment failed for orderId: {}", orderId);
        }
    }
}