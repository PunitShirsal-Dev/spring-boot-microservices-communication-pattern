package com.example.asynchronous.config;

import com.example.asynchronous.publisher.EventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.UUID;

@Configuration
public class EventPublisherConfig {

    @Bean
    public EventPublisher eventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        return new EventPublisher(kafkaTemplate);
    }
}
