package com.example.asynchronous.receiver;

import com.example.asynchronous.events.DomainEvent;
import com.example.asynchronous.events.EventType;
import com.example.asynchronous.events.OrderCancelledEvent;
import com.example.asynchronous.events.OrderCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import static com.example.asynchronous.events.EventType.ORDER_CANCELLED;
import static com.example.asynchronous.events.EventType.ORDER_CREATED;

@Component
@Slf4j
public class EventReceiver {

    @KafkaListener(
            topics = "${kafka.topics.orders}",
            groupId = "${spring.application.name}-product-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeOrderEvent(@Payload DomainEvent<?> event,
                                  @Header(KafkaHeaders.RECEIVED_KEY) String key,
                                  @Header("X-Correlation-ID") String correlationId,
                                  Acknowledgment acknowledgment) {

        MDC.put("correlationId", correlationId);

        try {
            switch (EventType.valueOf(event.getEventType())) {
                case ORDER_CREATED -> handleOrderCreated((OrderCreatedEvent) event.getPayload());
                case ORDER_CANCELLED -> handleOrderCancelled((OrderCancelledEvent) event.getPayload());
            }

            acknowledgment.acknowledge();
            log.info("Successfully processed event: {}", event.getEventId());

        } catch (Exception e) {
            log.error("Failed to process event: {}", event.getEventId(), e);
            // Send to DLQ or retry
        } finally {
            MDC.remove("correlationId");
        }
    }

    private void handleOrderCreated(OrderCreatedEvent event) {
        // Process payment for order
       log.info("Received order created: {}", event.getOrderId());
    }

    private void handleOrderCancelled(OrderCancelledEvent event) {
        // Refund payment
        log.info("Received order cancelled: {}", event.getOrderId());
    }
}
