package com.sassi.smokehabits.service;

import com.sassi.smokehabits.entity.CigaretteEntry;
import com.sassi.smokehabits.entity.User;
import com.sassi.smokehabits.repository.CigaretteEntryRepository;
import com.sassi.smokehabits.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CigaretteService {

    private final CigaretteEntryRepository repository;
    private final UserRepository userRepository;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public CigaretteService(CigaretteEntryRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    public CigaretteEntry logCigarette(UUID userId, int cravingLevel) {
        User user = userRepository.getUserById(userId);
        logger.debug(user.getId().toString());
        logger.debug(user.getEmail());
        CigaretteEntry entry = new CigaretteEntry(user, LocalDateTime.now(),  cravingLevel);
        return repository.save(entry);
    }

    public List<CigaretteEntry> getAll(UUID userId) {
        User user = userRepository.getUserById(userId);
        return repository.findAllByUserOrderByTimestampDesc(user);
    }
}

