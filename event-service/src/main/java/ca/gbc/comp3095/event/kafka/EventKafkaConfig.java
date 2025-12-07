package ca.gbc.comp3095.event.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventKafkaConfig {

    public static final String EVENT_CREATED_TOPIC = "wellness.events";

    @Bean
    public NewTopic eventCreatedTopic() {
        // 1 partition, replication factor 1 (single broker)
        return new NewTopic(EVENT_CREATED_TOPIC, 1, (short) 1);
    }
}
