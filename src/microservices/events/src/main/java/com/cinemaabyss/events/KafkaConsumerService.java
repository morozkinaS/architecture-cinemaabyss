package com.cinemaabyss.events;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    @KafkaListener(topics = "movie-events", groupId = "events-service")
    public void consumeMovieEvent(String message) {
        System.out.println("🎬 CONSUMED MOVIE EVENT: " + message);
    }

    @KafkaListener(topics = "user-events", groupId = "events-service")
    public void consumeUserEvent(String message) {
        System.out.println("👤 CONSUMED USER EVENT: " + message);
    }

    @KafkaListener(topics = "payment-events", groupId = "events-service")
    public void consumePaymentEvent(String message) {
        System.out.println("💳 CONSUMED PAYMENT EVENT: " + message);
    }
}