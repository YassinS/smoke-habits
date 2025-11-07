package com.sassi.smokehabits.service;

import com.sassi.smokehabits.dto.request.SmokeContextRequest;
import com.sassi.smokehabits.dto.response.SmokeContextResponse;
import com.sassi.smokehabits.entity.CigaretteEntry;
import com.sassi.smokehabits.entity.SmokeContext;
import com.sassi.smokehabits.entity.User;
import com.sassi.smokehabits.exception.AuthenticationError;
import com.sassi.smokehabits.repository.CigaretteEntryRepository;
import com.sassi.smokehabits.repository.SmokeContextRepository;
import com.sassi.smokehabits.repository.UserRepository;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

@Service
public class SmokeContextService {

    private static final Logger log = LoggerFactory.getLogger(
        SmokeContextService.class
    );
    private final SmokeContextRepository smokeContextRepository;
    private final UserRepository userRepository;
    private final CigaretteEntryRepository cigaretteEntryRepository;

    public SmokeContextService(
        SmokeContextRepository smokeContextRepository,
        UserRepository userRepository,
        CigaretteEntryRepository cigaretteEntryRepository
    ) {
        this.smokeContextRepository = smokeContextRepository;
        this.userRepository = userRepository;
        this.cigaretteEntryRepository = cigaretteEntryRepository;
    }

    /**
     * Retrieves all smoke contexts for a user as cached DTOs.
     * Results are cached per userId and invalidated when contexts are created, edited, or deleted.
     */
    @Cacheable(value = "smokeContexts", key = "#userId")
    public List<SmokeContextResponse> findAllByUUID(UUID userId) {
        log.debug("Fetching all smoke contexts for user: {}", userId);
        User user = userRepository.findById(userId).orElseThrow();
        List<SmokeContext> smokeContexts = smokeContextRepository.findAllByUser(
            user
        );

        List<SmokeContextResponse> responses = smokeContexts
            .stream()
            .map(SmokeContext::toSmokeContextResponse)
            .toList();

        log.debug(
            "Found {} smoke contexts for user: {}",
            responses.size(),
            userId
        );
        return responses;
    }

    /**
     * Retrieves a single smoke context by ID as a cached DTO.
     * Results are cached per context ID and invalidated when the context is edited or deleted.
     */
    @Cacheable(value = "smokeContext", key = "#contextId")
    public SmokeContextResponse findById(UUID contextId) {
        log.debug("Fetching smoke context: {}", contextId);
        SmokeContext smokeContext = smokeContextRepository
            .findById(contextId)
            .orElseThrow();
        return smokeContext.toSmokeContextResponse();
    }

    /**
     * Creates a new smoke context and invalidates the user's context list cache.
     */
    @CacheEvict(value = "smokeContexts", key = "#userId")
    public SmokeContextResponse createSmokeContext(
        UUID userId,
        SmokeContextRequest smokeContextRequest
    ) {
        log.debug("Creating new smoke context for user: {}", userId);
        User user = userRepository.findById(userId).orElseThrow();

        SmokeContext smokeContext = new SmokeContext();
        smokeContext.setContext(smokeContextRequest.getContext());
        smokeContext.setUser(user);
        smokeContext.setColorUI(smokeContextRequest.getColorUI());

        SmokeContext savedContext = smokeContextRepository.save(smokeContext);
        log.debug("Smoke context created with ID: {}", savedContext.getId());

        return savedContext.toSmokeContextResponse();
    }

    /**
     * Edits an existing smoke context and invalidates both the individual context cache
     * and the user's context list cache.
     */
    @Caching(
        evict = {
            @CacheEvict(value = "smokeContext", key = "#smokeContextToEdit.id"),
            @CacheEvict(
                value = "smokeContexts",
                key = "#smokeContextToEdit.user.id"
            ),
        }
    )
    public SmokeContextResponse editSmokeContext(
        SmokeContext smokeContextToEdit,
        SmokeContextRequest smokeContextRequest
    ) {
        log.debug("Editing smoke context: {}", smokeContextToEdit.getId());
        smokeContextToEdit.setColorUI(smokeContextRequest.getColorUI());
        smokeContextToEdit.setContext(smokeContextRequest.getContext());

        SmokeContext savedContext = smokeContextRepository.save(
            smokeContextToEdit
        );
        log.debug("Smoke context saved: {}", smokeContextToEdit.getId());

        return savedContext.toSmokeContextResponse();
    }

    /**
     * Deletes a smoke context after verifying ownership by the user.
     * Before deletion, nullifies all cigarette entries that reference this context
     * to avoid foreign key constraint violations.
     * Invalidates the individual context cache, the user's context list cache, and the cigarettes cache.
     * Throws AuthenticationError if the context does not belong to the user or is not found.
     */
    @Caching(
        evict = {
            @CacheEvict(value = "smokeContext", key = "#contextId"),
            @CacheEvict(value = "smokeContexts", key = "#userId"),
            @CacheEvict(value = "cigarettes", key = "#userId"),
        }
    )
    public void deleteSmokeContext(UUID contextId, UUID userId) {
        log.debug(
            "Attempting to delete smoke context: {} for user: {}",
            contextId,
            userId
        );

        User user = userRepository.findById(userId).orElseThrow();
        SmokeContext contextToDelete =
            smokeContextRepository.findSmokeContextByIdAndUser(contextId, user);

        if (contextToDelete == null) {
            log.warn(
                "Smoke context {} not found or does not belong to user: {}",
                contextId,
                userId
            );
            throw new AuthenticationError("Smoke context not found");
        }

        // Find all cigarette entries that reference this context
        List<CigaretteEntry> cigarettesWithContext =
            cigaretteEntryRepository.findAllByContext(contextToDelete);

        if (!cigarettesWithContext.isEmpty()) {
            log.debug(
                "Found {} cigarette entries referencing context: {}, nullifying references",
                cigarettesWithContext.size(),
                contextId
            );
            // Nullify the context reference for all cigarette entries
            cigarettesWithContext.forEach(entry -> entry.setContext(null));
            cigaretteEntryRepository.saveAll(cigarettesWithContext);
            log.debug(
                "Nullified context references for {} cigarette entries",
                cigarettesWithContext.size()
            );
        }

        smokeContextRepository.deleteById(contextId);
        log.info("Smoke context deleted successfully: {}", contextId);
    }
}
