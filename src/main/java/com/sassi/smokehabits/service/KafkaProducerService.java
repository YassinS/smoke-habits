package com.sassi.smokehabits.service;

import com.sassi.smokehabits.dto.kafka.CigaretteLoggedEvent;
import com.sassi.smokehabits.dto.kafka.SmokeContextCreatedEvent;
import com.sassi.smokehabits.dto.kafka.SmokeContextDeletedEvent;
import com.sassi.smokehabits.dto.kafka.SmokeContextUpdatedEvent;
import com.sassi.smokehabits.dto.kafka.UserDeletedEvent;
import com.sassi.smokehabits.dto.kafka.UserRegisteredEvent;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private static final Logger log = LoggerFactory.getLogger(
        KafkaProducerService.class
    );

    private final KafkaTemplate<String, Object> kafkaTemplate;

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

    public KafkaProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishCigaretteLoggedEvent(CigaretteLoggedEvent event) {
        log.info("Publishing cigarette logged event: {}", event);

        CompletableFuture<SendResult<String, Object>> future =
            kafkaTemplate.send(
                cigaretteLoggedTopic,
                event.getUserId().toString(),
                event
            );

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info(
                    "Cigarette logged event published successfully to partition {} with offset {}",
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset()
                );
            } else {
                log.error(
                    "Failed to publish cigarette logged event: {}",
                    ex.getMessage(),
                    ex
                );
            }
        });
    }

    public void publishUserRegisteredEvent(UserRegisteredEvent event) {
        log.info("Publishing user registered event: {}", event);

        CompletableFuture<SendResult<String, Object>> future =
            kafkaTemplate.send(
                userRegisteredTopic,
                event.getUserId().toString(),
                event
            );

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info(
                    "User registered event published successfully to partition {} with offset {}",
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset()
                );
            } else {
                log.error(
                    "Failed to publish user registered event: {}",
                    ex.getMessage(),
                    ex
                );
            }
        });
    }

    public void publishUserDeletedEvent(UserDeletedEvent event) {
        log.info("Publishing user deleted event: {}", event);

        CompletableFuture<SendResult<String, Object>> future =
            kafkaTemplate.send(
                userDeletedTopic,
                event.getUserId().toString(),
                event
            );

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info(
                    "User deleted event published successfully to partition {} with offset {}",
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset()
                );
            } else {
                log.error(
                    "Failed to publish user deleted event: {}",
                    ex.getMessage(),
                    ex
                );
            }
        });
    }

    public void publishSmokeContextCreatedEvent(
        SmokeContextCreatedEvent event
    ) {
        log.info("Publishing smoke context created event: {}", event);

        CompletableFuture<SendResult<String, Object>> future =
            kafkaTemplate.send(
                contextCreatedTopic,
                event.getUserId().toString(),
                event
            );

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info(
                    "Smoke context created event published successfully to partition {} with offset {}",
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset()
                );
            } else {
                log.error(
                    "Failed to publish smoke context created event: {}",
                    ex.getMessage(),
                    ex
                );
            }
        });
    }

    public void publishSmokeContextUpdatedEvent(
        SmokeContextUpdatedEvent event
    ) {
        log.info("Publishing smoke context updated event: {}", event);

        CompletableFuture<SendResult<String, Object>> future =
            kafkaTemplate.send(
                contextUpdatedTopic,
                event.getUserId().toString(),
                event
            );

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info(
                    "Smoke context updated event published successfully to partition {} with offset {}",
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset()
                );
            } else {
                log.error(
                    "Failed to publish smoke context updated event: {}",
                    ex.getMessage(),
                    ex
                );
            }
        });
    }

    public void publishSmokeContextDeletedEvent(
        SmokeContextDeletedEvent event
    ) {
        log.info("Publishing smoke context deleted event: {}", event);

        CompletableFuture<SendResult<String, Object>> future =
            kafkaTemplate.send(
                contextDeletedTopic,
                event.getUserId().toString(),
                event
            );

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info(
                    "Smoke context deleted event published successfully to partition {} with offset {}",
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset()
                );
            } else {
                log.error(
                    "Failed to publish smoke context deleted event: {}",
                    ex.getMessage(),
                    ex
                );
            }
        });
    }
}
