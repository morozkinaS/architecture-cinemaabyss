package com.cinemaabyss.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerService.class);

    @KafkaListener(topics = "movie-events", groupId = "events-service")
    public void consumeMovieEvent(String message) {
        log.info("🎬 CONSUMED MOVIE EVENT: {}", message);
    }

    @KafkaListener(topics = "user-events", groupId = "events-service")
    public void consumeUserEvent(String message) {
        log.info("👤 CONSUMED USER EVENT: {}", message);
    }

    @KafkaListener(topics = "payment-events", groupId = "events-service")
    public void consumePaymentEvent(String message) {
        log.info("💳 CONSUMED PAYMENT EVENT: {}", message);
    }
}