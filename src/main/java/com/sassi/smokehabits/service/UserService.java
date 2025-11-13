package com.sassi.smokehabits.service;

import com.sassi.smokehabits.dto.kafka.UserDeletedEvent;
import com.sassi.smokehabits.dto.kafka.UserRegisteredEvent;
import com.sassi.smokehabits.entity.User;
import com.sassi.smokehabits.repository.UserRepository;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(
        UserService.class
    );
    private final UserRepository userRepository;
    private final CacheManager cacheManager;
    private final KafkaProducerService kafkaProducerService;

    public UserService(
        UserRepository userRepository,
        CacheManager cacheManager,
        KafkaProducerService kafkaProducerService
    ) {
        this.userRepository = userRepository;
        this.cacheManager = cacheManager;
        this.kafkaProducerService = kafkaProducerService;
    }

    public void deleteUser(UUID userId) {
        log.info("Initiating user deletion for user: {}", userId);

        User user = userRepository.getUserById(userId);
        String userEmail = user.getEmail();

        // Clear all user-related caches
        clearUserCaches(userId);

        // Delete the user
        userRepository.delete(user);
        log.info("User deleted successfully: {}", userId);

        // Publish event to Kafka for analytics service
        UserDeletedEvent event = new UserDeletedEvent(
            userId,
            userEmail,
            Instant.now()
        );
        kafkaProducerService.publishUserDeletedEvent(event);
    }

    private void clearUserCaches(UUID userId) {
        log.debug("Clearing all caches for user: {}", userId);

        if (cacheManager.getCache("cigarettes") != null) {
            Objects.requireNonNull(cacheManager.getCache("cigarettes")).evict(
                userId
            );
            log.debug("Cleared 'cigarettes' cache for user: {}", userId);
        }

        if (cacheManager.getCache("cigaretteResponses") != null) {
            Objects.requireNonNull(
                cacheManager.getCache("cigaretteResponses")
            ).evict(userId);
            log.debug(
                "Cleared 'cigaretteResponses' cache for user: {}",
                userId
            );
        }

        if (cacheManager.getCache("smokeContexts") != null) {
            Objects.requireNonNull(
                cacheManager.getCache("smokeContexts")
            ).evict(userId);
            log.debug("Cleared 'smokeContexts' cache for user: {}", userId);
        }

        // Clear analytics caches
        String[] analyticsCaches = {
            "analytics:daily",
            "analytics:weekly",
            "analytics:monthly",
            "analytics:context",
            "analytics:longestStreak",
            "analytics:avgCraving",
        };

        for (String cacheName : analyticsCaches) {
            if (cacheManager.getCache(cacheName) != null) {
                Objects.requireNonNull(cacheManager.getCache(cacheName)).evict(
                    userId
                );
                log.debug("Cleared '{}' cache for user: {}", cacheName, userId);
            }
        }
    }
}
