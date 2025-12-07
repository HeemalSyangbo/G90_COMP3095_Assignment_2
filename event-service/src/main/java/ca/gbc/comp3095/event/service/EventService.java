package ca.gbc.comp3095.event.service;

import ca.gbc.comp3095.event.kafka.EventProducer;
import ca.gbc.comp3095.event.model.Event;
import ca.gbc.comp3095.event.repo.EventRepository;
import ca.gbc.comp3095.event.web.dto.ResourceResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EventService {

    private final EventRepository repo;
    private final WebClient wellnessWebClient;
    private final EventProducer producer;   // Kafka producer

    // Inject WebClient bean + Kafka producer
    public EventService(EventRepository repo,
                        WebClient wellnessWebClient,
                        EventProducer producer) {
        this.repo = repo;
        this.wellnessWebClient = wellnessWebClient;
        this.producer = producer;
    }

    public List<Event> findAll(Optional<LocalDate> date, Optional<String> location) {
        if (date.isPresent() && location.isPresent()) {
            LocalDate d = date.get();
            LocalDateTime start = d.atStartOfDay();
            LocalDateTime end = d.plusDays(1).atStartOfDay().minusNanos(1);
            return repo.findByDateBetween(start, end).stream()
                    .filter(e -> e.getLocation().equalsIgnoreCase(location.get()))
                    .sorted(Comparator.comparing(Event::getDate))
                    .collect(Collectors.toList());
        }
        if (date.isPresent()) {
            LocalDate d = date.get();
            return repo.findByDateBetween(
                    d.atStartOfDay(),
                    d.plusDays(1).atStartOfDay().minusNanos(1)
            );
        }
        if (location.isPresent()) {
            return repo.findByLocationIgnoreCase(location.get());
        }
        return repo.findAll();
    }

    public Optional<Event> findById(UUID id) {
        return repo.findById(id);
    }

    public Event create(Event e) {
        // ensure ID set
        if (e.getId() == null) {
            e.setId(UUID.randomUUID());
        }

        Event saved = repo.save(e);

        // After saving, publish Kafka message
        producer.sendEventCreated(saved);

        return saved;
    }

    public Optional<Event> update(UUID id, Event updated) {
        return repo.findById(id).map(old -> {
            updated.setId(id);
            // keep existing registrations
            updated.getRegisteredStudentIds().addAll(old.getRegisteredStudentIds());
            return repo.save(updated);
        });
    }

    public boolean delete(UUID id) {
        if (!repo.existsById(id)) return false;
        repo.deleteById(id);
        // (optional) send a "deleted" event later if needed
        return true;
    }

    public Optional<Event> register(UUID id, String studentId) {
        return repo.findById(id).map(e -> {
            if (e.getRegisteredCount() < e.getCapacity()) {
                e.getRegisteredStudentIds().add(studentId);
                return repo.save(e);
            }
            return e; // full; still return current state
        });
    }

    public Optional<Event> unregister(UUID id, String studentId) {
        return repo.findById(id).map(e -> {
            e.getRegisteredStudentIds().remove(studentId);
            return repo.save(e);
        });
    }

    // ---------- Inter-service call to wellness-resource-service ----------
    // Resilience4j: CircuitBreaker + Retry with fallback

    @CircuitBreaker(name = "wellnessResources", fallbackMethod = "fallbackResourcesForCategory")
    @Retry(name = "wellnessResources")
    public List<ResourceResponse> fetchResourcesForCategory(String category) {
        if (category == null || category.isBlank()) return Collections.emptyList();

        return wellnessWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/resources")
                        .queryParam("category", category)
                        .build())
                .retrieve()
                .bodyToFlux(ResourceResponse.class)
                .collectList()
                .block(); // simple synchronous call is fine here
    }

    // Fallback method when wellness-service is down/slow
    @SuppressWarnings("unused")
    private List<ResourceResponse> fallbackResourcesForCategory(String category, Throwable t) {
        System.err.println("⚠️ Fallback triggered for category '" + category +
                "' due to: " + t.getClass().getSimpleName() + " - " + t.getMessage());
        // Safe default – no resources instead of breaking the event-service
        return Collections.emptyList();
    }
}
