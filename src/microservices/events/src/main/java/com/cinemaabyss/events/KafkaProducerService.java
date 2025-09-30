package com.cinemaabyss.events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendMovieEvent(String eventData) {
        kafkaTemplate.send("movie-events", eventData);
        System.out.println("Sent to movie-events: " + eventData);
    }

    public void sendUserEvent(String eventData) {
        kafkaTemplate.send("user-events", eventData);
        System.out.println("Sent to user-events: " + eventData);
    }

    public void sendPaymentEvent(String eventData) {
        kafkaTemplate.send("payment-events", eventData);
        System.out.println("Sent to payment-events: " + eventData);
    }
}