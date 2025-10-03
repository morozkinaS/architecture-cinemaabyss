package com.cinemaabyss.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/events")
public class EventsController {

    @Autowired
    private KafkaProducerService kafkaProducerService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    @PostMapping("/movie")
    public ResponseEntity<Map<String, Object>> createMovieEvent(@RequestBody Map<String, Object> eventData) {
        try {
            String eventId = UUID.randomUUID().toString();

            Map<String, Object> event = new HashMap<>();
            event.put("id", eventId);
            event.put("type", "movie");
            event.put("timestamp", LocalDateTime.now().format(formatter));
            event.put("payload", eventData);

            // Отправляем в Kafka
            kafkaProducerService.sendMovieEvent(objectMapper.writeValueAsString(event));

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("partition", 0);
            response.put("offset", System.currentTimeMillis());
            response.put("event", event);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to create movie event: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/user")
    public ResponseEntity<Map<String, Object>> createUserEvent(@RequestBody Map<String, Object> eventData) {
        try {
            String eventId = UUID.randomUUID().toString();

            Map<String, Object> event = new HashMap<>();
            event.put("id", eventId);
            event.put("type", "user");
            event.put("timestamp", LocalDateTime.now().format(formatter));
            event.put("payload", eventData);

            // Отправляем в Kafka
            kafkaProducerService.sendUserEvent(objectMapper.writeValueAsString(event));

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("partition", 0);
            response.put("offset", System.currentTimeMillis());
            response.put("event", event);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to create user event: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/payment")
    public ResponseEntity<Map<String, Object>> createPaymentEvent(@RequestBody Map<String, Object> eventData) {
        try {
            String eventId = UUID.randomUUID().toString();

            Map<String, Object> event = new HashMap<>();
            event.put("id", eventId);
            event.put("type", "payment");
            event.put("timestamp", LocalDateTime.now().format(formatter));
            event.put("payload", eventData);

            // Отправляем в Kafka
            kafkaProducerService.sendPaymentEvent(objectMapper.writeValueAsString(event));

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("partition", 0);
            response.put("offset", System.currentTimeMillis());
            response.put("event", event);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to create payment event: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}