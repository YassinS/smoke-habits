package com.sassi.smokehabits.service;

import com.sassi.smokehabits.dto.response.CigaretteResponse;
import com.sassi.smokehabits.dto.response.SmokeContextResponse;
import com.sassi.smokehabits.entity.CigaretteEntry;
import com.sassi.smokehabits.entity.SmokeContext;
import com.sassi.smokehabits.entity.User;
import com.sassi.smokehabits.repository.CigaretteEntryRepository;
import com.sassi.smokehabits.repository.UserRepository;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class CigaretteService {

    private final CigaretteEntryRepository repository;
    private final UserRepository userRepository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public CigaretteService(
        CigaretteEntryRepository repository,
        UserRepository userRepository
    ) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @CacheEvict(value = "cigaretteResponses", key = "#userId")
    public CigaretteResponse logCigarette(
        UUID userId,
        int cravingLevel,
        SmokeContext context
    ) {
        User user = userRepository.getUserById(userId);
        logger.debug("Logging cigarette for user: {}", user.getId());
        logger.debug("User email: {}", user.getEmail());

        CigaretteEntry entry = new CigaretteEntry(user, cravingLevel, context);
        CigaretteEntry savedEntry = repository.save(entry);

        logger.debug("Cigarette logged with ID: {}", savedEntry.getId());
        return mapToResponse(savedEntry);
    }

    @CacheEvict(value = "cigaretteResponses", key = "#userId")
    public CigaretteResponse logCigarette(UUID userId, int cravingLevel) {
        User user = userRepository.getUserById(userId);
        logger.debug("Logging cigarette for user: {}", user.getId());
        logger.debug("User email: {}", user.getEmail());

        CigaretteEntry entry = new CigaretteEntry(user, cravingLevel);
        CigaretteEntry savedEntry = repository.save(entry);

        logger.debug("Cigarette logged with ID: {}", savedEntry.getId());
        return mapToResponse(savedEntry);
    }

    @Cacheable(value = "cigaretteResponses", key = "#userId")
    public List<CigaretteResponse> getAll(UUID userId) {
        User user = userRepository.getUserById(userId);
        logger.debug("Fetching all cigarettes for user: {}", userId);

        List<CigaretteEntry> entries =
            repository.findAllByUserOrderByTimestampDesc(user);
        logger.debug(
            "Found {} cigarette entries for user: {}",
            entries.size(),
            userId
        );

        List<CigaretteResponse> responses = entries
            .stream()
            .map(this::mapToResponse)
            .toList();

        logger.debug(
            "Converted to {} CigaretteResponse DTOs and cached",
            responses.size()
        );
        return responses;
    }


    public List<CigaretteEntry> getAllEntities(UUID userId) {
        User user = userRepository.getUserById(userId);
        return repository.findAllByUserOrderByTimestampDesc(user);
    }


    private CigaretteResponse mapToResponse(CigaretteEntry entry) {
        SmokeContextResponse contextResponse = null;

        if (entry.getContext() != null) {
            SmokeContext context = entry.getContext();
            contextResponse = new SmokeContextResponse(
                context.getId(),
                context.getContext(),
                context.getColorUI()
            );
        }

        return new CigaretteResponse(
            entry.getId(),
            entry.getTimestamp(),
            entry.getCravingLevel(),
            contextResponse
        );
    }
}
