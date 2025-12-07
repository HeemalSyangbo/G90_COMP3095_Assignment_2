package ca.gbc.comp3095.event.kafka;

import ca.gbc.comp3095.event.model.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class EventProducer {

    private static final Logger log = LoggerFactory.getLogger(EventProducer.class);

    private final KafkaTemplate<String, EventCreatedMessage> template;

    public EventProducer(KafkaTemplate<String, EventCreatedMessage> template) {
        this.template = template;
    }

    public void sendEventCreated(Event event) {
        EventCreatedMessage msg = new EventCreatedMessage(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getDate(),
                event.getLocation(),
                event.getCategory(),
                event.getCapacity()
        );

        template.send(EventKafkaConfig.EVENT_CREATED_TOPIC,
                event.getId().toString(),
                msg);

        log.info("📤 Sent EventCreated to Kafka for event {}", event.getId());
    }
}
