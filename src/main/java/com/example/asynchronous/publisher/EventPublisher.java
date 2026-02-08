package com.example.asynchronous.publisher;


import com.example.asynchronous.events.DomainEvent;
import com.example.asynchronous.events.EventType;
import com.example.asynchronous.events.OrderCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class EventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public EventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishOrderCreated(OrderCreatedEvent event) {
        DomainEvent<OrderCreatedEvent> domainEvent = DomainEvent.<OrderCreatedEvent>builder()
                .eventId(UUID.randomUUID().toString())
                .eventType(EventType.ORDER_CREATED.name())
                .aggregateId(event.getOrderId())
                .aggregateType("Order")
                .timestamp(Instant.now())
                .source("order-service")
                .payload(event)
                .metadata(Map.of(
                        "correlationId", MDC.get("correlationId"),
                        "userId", SecurityContextHolder.getContext().getAuthentication().getName()
                ))
                .build();

        publishEvent(domainEvent);
    }

    private void publishEvent(DomainEvent<?> event) {
        Message<DomainEvent<?>> message = new GenericMessage<>(event);
        CompletableFuture.runAsync(() -> kafkaTemplate.send("orders", message));
    }

    // Transactional publishing
    @Transactional
    public void publishOrderCreatedTransactional(Order order) {
        // Save order to database

        // Publish event
        OrderCreatedEvent event = OrderCreatedEvent
                .builder()
                .orderId("order-id")
                .eventId(UUID.randomUUID().toString())
                .createdAt(Instant.now())
                .build();

        publishOrderCreated(event);
    }
}
