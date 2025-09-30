package com.cinemaabyss.proxy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Random;

@RestController
@RequestMapping
public class ProxyController {

    private final WebClient monolithClient;
    private final WebClient moviesClient;
    private final WebClient eventsClient;
    private final int moviesMigrationPercent;
    private final Random random = new Random();
    private final boolean gradualMigration;

    public ProxyController(
            @Value("${MONOLITH_URL}") String monolithUrl,
            @Value("${MOVIES_SERVICE_URL}") String moviesUrl,
            @Value("${EVENTS_SERVICE_URL}") String eventsUrl,
            @Value("${MOVIES_MIGRATION_PERCENT:50}") String migrationPercent,
            @Value("${GRADUAL_MIGRATION:true}") String gradualMigration) {
        this.monolithClient = WebClient.builder().baseUrl(monolithUrl).build();
        this.moviesClient = WebClient.builder().baseUrl(moviesUrl).build();
        this.eventsClient = WebClient.builder().baseUrl(eventsUrl).build();
        this.moviesMigrationPercent = Integer.parseInt(migrationPercent);
        this.gradualMigration = Boolean.parseBoolean(gradualMigration);
    }

    @GetMapping("/health")
    public String health() {
        return "Strangler Fig Proxy is healthy";
    }

    // Movies endpoints
    @GetMapping("/api/movies")
    public Mono<ResponseEntity<String>> getMovies() {
        return routeRequest("/api/movies", "GET", null);
    }

    @PostMapping("/api/movies")
    public Mono<ResponseEntity<String>> createMovie(@RequestBody String body) {
        return routeRequest("/api/movies", "POST", body);
    }

    // Users endpoints
    @GetMapping("/api/users")
    public Mono<ResponseEntity<String>> getUsers() {
        return monolithRequest("/api/users", "GET", null);
    }

    @PostMapping("/api/users")
    public Mono<ResponseEntity<String>> createUser(@RequestBody String body) {
        return monolithRequest("/api/users", "POST", body);
    }

    // Payments endpoints
    @GetMapping("/api/payments")
    public Mono<ResponseEntity<String>> getPayments() {
        return monolithRequest("/api/payments", "GET", null);
    }

    @PostMapping("/api/payments")
    public Mono<ResponseEntity<String>> createPayment(@RequestBody String body) {
        return monolithRequest("/api/payments", "POST", body);
    }

    // Subscriptions endpoints
    @GetMapping("/api/subscriptions")
    public Mono<ResponseEntity<String>> getSubscriptions() {
        return monolithRequest("/api/subscriptions", "GET", null);
    }

    @PostMapping("/api/subscriptions")
    public Mono<ResponseEntity<String>> createSubscription(@RequestBody String body) {
        return monolithRequest("/api/subscriptions", "POST", body);
    }

    // Events endpoints
    @GetMapping("/api/events/health")
    public Mono<ResponseEntity<String>> getEventsHealth() {
        return eventsRequest("/api/events/health", "GET", null);
    }

    @PostMapping("/api/events/movie")
    public Mono<ResponseEntity<String>> createMovieEvent(@RequestBody String body) {
        return eventsRequest("/api/events/movie", "POST", body);
    }

    @PostMapping("/api/events/user")
    public Mono<ResponseEntity<String>> createUserEvent(@RequestBody String body) {
        return eventsRequest("/api/events/user", "POST", body);
    }

    @PostMapping("/api/events/payment")
    public Mono<ResponseEntity<String>> createPaymentEvent(@RequestBody String body) {
        return eventsRequest("/api/events/payment", "POST", body);
    }

    private Mono<ResponseEntity<String>> routeRequest(String path, String method, String body) {
        boolean useNewService = !gradualMigration || random.nextInt(100) < moviesMigrationPercent;
        WebClient client = useNewService ? moviesClient : monolithClient;

        return makeRequest(client, path, method, body);
    }

    private Mono<ResponseEntity<String>> monolithRequest(String path, String method, String body) {
        return makeRequest(monolithClient, path, method, body);
    }

    private Mono<ResponseEntity<String>> eventsRequest(String path, String method, String body) {
        return makeRequest(eventsClient, path, method, body);
    }

    private Mono<ResponseEntity<String>> makeRequest(WebClient client, String path, String method, String body) {
        WebClient.RequestBodySpec request = client.method(HttpMethod.valueOf(method))
                .uri(path)
                .contentType(MediaType.APPLICATION_JSON);

        if (body != null && (method.equals("POST") || method.equals("PUT"))) {
            request.bodyValue(body);
        }

        return request.retrieve()
                .toEntity(String.class)
                .onErrorResume(e -> Mono.just(ResponseEntity.status(500)
                        .body("{\"error\": \"Service unavailable: " + e.getMessage() + "\"}")));
    }
}