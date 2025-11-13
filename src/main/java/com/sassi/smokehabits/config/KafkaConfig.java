package com.sassi.smokehabits.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Value("${kafka.topic.cigarette-logged}")
    private String cigaretteLoggedTopic;

    @Value("${kafka.topic.user-registered}")
    private String userRegisteredTopic;

    @Value("${kafka.topic.user-deleted}")
    private String userDeletedTopic;

    @Value("${kafka.topic.context-created}")
    private String contextCreatedTopic;

    @Value("${kafka.topic.context-updated}")
    private String contextUpdatedTopic;

    @Value("${kafka.topic.context-deleted}")
    private String contextDeletedTopic;

    @Bean
    public NewTopic cigaretteLoggedTopic() {
        return TopicBuilder.name(cigaretteLoggedTopic)
            .partitions(3)
            .replicas(1)
            .build();
    }

    @Bean
    public NewTopic userRegisteredTopic() {
        return TopicBuilder.name(userRegisteredTopic)
            .partitions(1)
            .replicas(1)
            .build();
    }

    @Bean
    public NewTopic userDeletedTopic() {
        return TopicBuilder.name(userDeletedTopic)
            .partitions(1)
            .replicas(1)
            .build();
    }

    @Bean
    public NewTopic contextCreatedTopic() {
        return TopicBuilder.name(contextCreatedTopic)
            .partitions(1)
            .replicas(1)
            .build();
    }

    @Bean
    public NewTopic contextUpdatedTopic() {
        return TopicBuilder.name(contextUpdatedTopic)
            .partitions(1)
            .replicas(1)
            .build();
    }

    @Bean
    public NewTopic contextDeletedTopic() {
        return TopicBuilder.name(contextDeletedTopic)
            .partitions(1)
            .replicas(1)
            .build();
    }
}
