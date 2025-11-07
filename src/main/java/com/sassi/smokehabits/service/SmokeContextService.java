package com.sassi.smokehabits.service;

import com.sassi.smokehabits.dto.request.SmokeContextRequest;
import com.sassi.smokehabits.dto.response.SmokeContextResponse;
import com.sassi.smokehabits.entity.SmokeContext;
import com.sassi.smokehabits.entity.User;
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
    private SmokeContextRepository smokeContextRepository;
    private UserRepository userRepository;

    public SmokeContextService(
        SmokeContextRepository smokeContextRepository,
        UserRepository userRepository
    ) {
        this.smokeContextRepository = smokeContextRepository;
        this.userRepository = userRepository;
    }

    @Cacheable(value = "smokeContexts", key = "#userId")
    public List<SmokeContextResponse> findAllByUserId(UUID userId) {
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


    @Cacheable(value = "smokeContext", key = "#contextId")
    public SmokeContextResponse findById(UUID contextId) {
        log.debug("Fetching smoke context: {}", contextId);
        SmokeContext smokeContext = smokeContextRepository
            .findById(contextId)
            .orElseThrow();
        return smokeContext.toSmokeContextResponse();
    }

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

        smokeContextRepository.save(smokeContextToEdit);
        log.debug("Smoke context saved: {}", smokeContextToEdit.getId());

        return smokeContextToEdit.toSmokeContextResponse();
    }
}
