package com.example.asynchronous.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DomainEvent<T> {
    private String eventId;
    private String eventType;
    private String aggregateId;
    private String aggregateType;
    private Instant timestamp;
    private String source;
    private T payload;
    private Map<String, String> metadata;
}
