package ca.gbc.wellnessresourceservice.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

// 👇 IMPORTANT: use the *same* package as above
import ca.gbc.comp3095.event.kafka.EventCreatedMessage;

@Component
public class EventCreatedListener {

    @KafkaListener(topics = "event-created-topic", groupId = "wellness-group")
    public void handle(EventCreatedMessage msg) {
        System.out.println("🟢 Wellness Service received EventCreated:");
        System.out.println("   ID       : " + msg.getEventId());
        System.out.println("   Title    : " + msg.getTitle());
        System.out.println("   Category : " + msg.getCategory());
    }
}
