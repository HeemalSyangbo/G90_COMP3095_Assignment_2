package ca.gbc.comp3095.event.kafka;   // 👈 IMPORTANT: must match producer package

import java.time.LocalDateTime;
import java.util.UUID;

public class EventCreatedMessage {

    private UUID eventId;
    private String title;
    private String description;
    private LocalDateTime date;
    private String location;
    private String category;
    private Integer capacity;

    // Default constructor (needed for JSON deserialization)
    public EventCreatedMessage() {
    }

    // Full constructor (nice for tests / manual use)
    public EventCreatedMessage(UUID eventId,
                               String title,
                               String description,
                               LocalDateTime date,
                               String location,
                               String category,
                               Integer capacity) {
        this.eventId = eventId;
        this.title = title;
        this.description = description;
        this.date = date;
        this.location = location;
        this.category = category;
        this.capacity = capacity;
    }

    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
}
